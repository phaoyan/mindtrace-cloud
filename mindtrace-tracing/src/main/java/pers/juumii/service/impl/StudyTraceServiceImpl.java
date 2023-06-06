package pers.juumii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.StudyTrace;
import pers.juumii.data.TraceCoverage;
import pers.juumii.dto.StudyTraceDTO;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceCoverageMapper;
import pers.juumii.service.StudyTraceService;

import java.util.List;

@Service
public class StudyTraceServiceImpl implements StudyTraceService {

    private final StudyTraceMapper studyTraceMapper;
    private final TraceCoverageMapper traceCoverageMapper;

    @Autowired
    public StudyTraceServiceImpl(
            StudyTraceMapper studyTraceMapper,
            TraceCoverageMapper traceCoverageMapper) {
        this.studyTraceMapper = studyTraceMapper;
        this.traceCoverageMapper = traceCoverageMapper;
    }

    @Override
    @Transactional
    public StudyTrace postStudyTrace(StudyTraceDTO data) {
        return data.getId() != null ? updateStudyTrace(data) : insertStudyTrace(data);
    }

    private StudyTrace insertStudyTrace(StudyTraceDTO data) {
        StudyTrace trace = StudyTrace.prototype(data);
        studyTraceMapper.insert(trace);
        return trace;
    }

    private StudyTrace updateStudyTrace(StudyTraceDTO data) {
        studyTraceMapper.updateById(StudyTrace.prototype(data));
        return null;
    }

    @Override
    public List<StudyTrace> getUserStudyTraces(Long userId) {
        LambdaQueryWrapper<StudyTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTrace::getUserId, userId);
        return studyTraceMapper.selectList(wrapper);
    }

    @Override
    public List<StudyTrace> getTemplateStudyTraces(Long templateId) {
        LambdaQueryWrapper<StudyTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTrace::getTemplateId, templateId);
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
}
