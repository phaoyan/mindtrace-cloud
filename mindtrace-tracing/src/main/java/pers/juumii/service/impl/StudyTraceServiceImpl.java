package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceCoverage;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.StudyTraceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceCoverageMapper;
import pers.juumii.service.StudyTraceService;
import pers.juumii.utils.DataUtils;

import java.util.List;

@Service
public class StudyTraceServiceImpl implements StudyTraceService {

    private final StudyTraceMapper studyTraceMapper;
    private final TraceCoverageMapper traceCoverageMapper;
    private final CoreClient coreClient;

    @Autowired
    public StudyTraceServiceImpl(
            StudyTraceMapper studyTraceMapper,
            TraceCoverageMapper traceCoverageMapper,
            CoreClient coreClient) {
        this.studyTraceMapper = studyTraceMapper;
        this.traceCoverageMapper = traceCoverageMapper;
        this.coreClient = coreClient;
    }

    @Override
    @Transactional
    public StudyTrace postStudyTrace(StudyTraceDTO data) {
        return data.getId() != null ? updateStudyTrace(data) : insertStudyTrace(data);
    }

    @Override
    @Transactional
    public StudyTrace insertStudyTrace(StudyTraceDTO data) {
        StudyTrace trace = StudyTrace.transfer(data);
        studyTraceMapper.insert(trace);
        return trace;
    }

    @Override
    @Transactional
    public StudyTrace updateStudyTrace(StudyTraceDTO data) {
        studyTraceMapper.updateById(StudyTrace.transfer(data));
        return studyTraceMapper.selectById(data.getId());
    }

    @Override
    public List<StudyTrace> getUserStudyTraces(Long userId) {
        if(userId == null)
            userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<StudyTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTrace::getUserId, userId);
        return studyTraceMapper.selectList(wrapper);
    }

    @Override
    public StudyTrace getStudyTrace(Long traceId) {
        return studyTraceMapper.selectById(traceId);
    }

    @Override
    @Transactional
    public void removeStudyTrace(Long traceId) {
        studyTraceMapper.deleteById(traceId);
        LambdaUpdateWrapper<TraceCoverage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceCoverage::getTraceId, traceId);
        traceCoverageMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void postTraceCoverage(Long traceId, Long knodeId) {
        if(!checkTraceCoverage(traceId, knodeId))
            traceCoverageMapper.insert(TraceCoverage.prototype(traceId, knodeId));
    }

    @Override
    public List<Long> getTraceCoverages(Long traceId) {
        LambdaQueryWrapper<TraceCoverage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceCoverage::getTraceId, traceId);
        return traceCoverageMapper.selectList(wrapper)
                .stream().map(TraceCoverage::getKnodeId)
                .toList();
    }

    @Override
    public Boolean checkTraceCoverage(Long traceId, Long knodeId) {
        LambdaQueryWrapper<TraceCoverage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceCoverage::getTraceId, traceId).eq(TraceCoverage::getKnodeId, knodeId);
        return traceCoverageMapper.exists(wrapper);
    }

    @Override
    public List<Long> getKnodeCoveringTraces(Long knodeId) {
        LambdaQueryWrapper<TraceCoverage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceCoverage::getKnodeId, knodeId);
        return traceCoverageMapper.selectList(wrapper)
                .stream().map(TraceCoverage::getTraceId)
                .toList();
    }

    @Override
    @Transactional
    public void removeTraceCoverage(Long traceId, Long knodeId) {
        LambdaQueryWrapper<TraceCoverage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceCoverage::getTraceId, traceId).eq(TraceCoverage::getKnodeId, knodeId);
        TraceCoverage coverage = traceCoverageMapper.selectOne(wrapper);
        if(coverage != null)
            traceCoverageMapper.deleteById(coverage);
    }

    @Override
    public List<StudyTrace> getStudyTracesOfKnode(Long knodeId) {
        List<StudyTrace> traces = getUserStudyTraces(null);
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        return traces.stream().filter(trace->
            !DataUtils.intersection(
                getTraceCoverages(trace.getId()),
                offsprings.stream().map(offspring->
                    Convert.toLong(offspring.getId()))
                    .toList()).isEmpty())
            .toList();
    }
}
