package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.Milestone;
import pers.juumii.data.persistent.MilestoneResourceRel;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.MilestoneMapper;
import pers.juumii.mapper.MilestoneResourceRelMapper;
import pers.juumii.service.MilestoneService;
import pers.juumii.utils.TimeUtils;

import java.util.List;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final MilestoneMapper milestoneMapper;
    private final MilestoneResourceRelMapper mrrMapper;


    @Autowired
    public MilestoneServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            MilestoneMapper milestoneMapper,
            MilestoneResourceRelMapper mrrMapper) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.milestoneMapper = milestoneMapper;
        this.mrrMapper = mrrMapper;
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
}
