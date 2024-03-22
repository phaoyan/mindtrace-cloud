package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.Milestone;
import pers.juumii.data.persistent.MilestoneResourceRel;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.MilestoneMapper;
import pers.juumii.mapper.MilestoneResourceRelMapper;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.service.MilestoneService;
import pers.juumii.utils.TimeUtils;

import java.util.List;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final MilestoneMapper milestoneMapper;
    private final MilestoneResourceRelMapper mrrMapper;
    private final StudyTraceMapper studyTraceMapper;


    @Autowired
    public MilestoneServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            MilestoneMapper milestoneMapper,
            MilestoneResourceRelMapper mrrMapper,
            StudyTraceMapper studyTraceMapper) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.milestoneMapper = milestoneMapper;
        this.mrrMapper = mrrMapper;
        this.studyTraceMapper = studyTraceMapper;
    }

    @Override
    @Transactional
    public Milestone add(Long knodeId, Long userId) {
        Milestone prototype = Milestone.prototype(knodeId, userId);
        milestoneMapper.insert(prototype);
        return prototype;
    }

    @Override
    @Transactional
    public void remove(Long id) {
        milestoneMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDescription(Long id, String desc) {
        Milestone milestone = milestoneMapper.selectById(id);
        milestone.setDescription(desc);
        milestoneMapper.updateById(milestone);
    }

    @Override
    @Transactional
    public void setKnodeId(Long id, Long knodeId) {
        Milestone milestone = milestoneMapper.selectById(id);
        milestone.setKnodeId(knodeId);
        milestoneMapper.updateById(milestone);
    }

    @Override
    @Transactional
    public void setTime(Long id, String dateTime) {
        Milestone milestone = milestoneMapper.selectById(id);
        milestone.setTime(TimeUtils.parse(dateTime));
        milestoneMapper.updateById(milestone);
    }

    @Override
    @Transactional
    public ResourceDTO addResource(Long id, String type) {
        ResourceDTO resource = enhancerClient.addResource(type);
        mrrMapper.insert(MilestoneResourceRel.prototype(id, Convert.toLong(resource.getId())));
        return resource;
    }

    @Override
    @Transactional
    public void removeResource(Long resourceId) {
        LambdaUpdateWrapper<MilestoneResourceRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MilestoneResourceRel::getResourceId, resourceId);
        mrrMapper.delete(wrapper);
        enhancerClient.removeResourceById(resourceId);
    }


    @Override
    public Milestone getById(Long id) {
        return milestoneMapper.selectById(id);
    }

    @Override
    public List<Milestone> getMilestonesBeneathKnode(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Milestone::getUserId, Convert.toLong(knode.getCreateBy()));
        List<Milestone> milestones = milestoneMapper.selectList(wrapper);
        return milestones.stream()
                .filter(milestone -> coreClient.isOffspring(milestone.getKnodeId(), knodeId))
                .toList();
    }

    @Override
    public List<ResourceDTO> getResourcesFromMilestone(Long id) {
        LambdaQueryWrapper<MilestoneResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MilestoneResourceRel::getMilestoneId, id);
        List<MilestoneResourceRel> rels = mrrMapper.selectList(wrapper);
        return rels.stream().map(rel->enhancerClient.getResourceById(rel.getResourceId())).toList();
    }

    @Override
    @Transactional
    public void addStudyTrace(Long milestoneId, Long traceId) {
        StudyTrace trace = studyTraceMapper.selectById(traceId);
        trace.setMilestoneId(milestoneId);
        studyTraceMapper.updateById(trace);
    }

    @Override
    @Transactional
    public void removeStudyTrace(Long milestoneId, Long traceId) {
        StudyTrace trace = studyTraceMapper.selectById(traceId);
        trace.setMilestoneId(null);
        studyTraceMapper.updateById(trace);
    }

    @Override
    public List<StudyTrace> getStudyTraces(Long milestoneId) {
        LambdaQueryWrapper<StudyTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTrace::getMilestoneId, milestoneId);
        return studyTraceMapper.selectList(wrapper);

    }

    @Override
    public Milestone getMilestone(Long traceId) {
        StudyTrace trace = studyTraceMapper.selectById(traceId);
        return milestoneMapper.selectById(trace.getMilestoneId());
    }

    @Override
    public void copyMilestoneAsEnhancerToKnode(Long milestoneId, Long knodeId) {
        List<ResourceDTO> resources = getResourcesFromMilestone(milestoneId);
        Milestone milestone = getById(milestoneId);
        EnhancerDTO enhancer = enhancerClient.addEnhancer();
        Long enhancerId = Convert.toLong(enhancer.getId());
        enhancerClient.addKnodeEnhancerRel(knodeId, enhancerId);
        for(ResourceDTO resource: resources)
            enhancerClient.addEnhancerResourceRel(enhancerId, Convert.toLong(resource.getId()));
        enhancer.setTitle(milestone.getDescription());
        enhancerClient.updateEnhancer(enhancerId, enhancer);
    }

    @Override
    public Milestone getMilestoneByResourceId(Long resourceId) {
        LambdaQueryWrapper<MilestoneResourceRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MilestoneResourceRel::getResourceId, resourceId);
        MilestoneResourceRel rel = mrrMapper.selectOne(wrapper);
        return getById(rel.getMilestoneId());
    }

}