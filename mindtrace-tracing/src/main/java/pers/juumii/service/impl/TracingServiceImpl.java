package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Mindtrace;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.mapper.MindtraceMapper;
import pers.juumii.service.TracingService;


@Service
public class TracingServiceImpl implements TracingService {

    private final MindtraceMapper mindtraceMapper;

    @Autowired
    public TracingServiceImpl(MindtraceMapper mindtraceMapper) {
        this.mindtraceMapper = mindtraceMapper;
    }


    @Override
    public SaResult record(Long userId, MindtraceDTO dto) {
        Mindtrace prototype = Mindtrace.prototype(dto);
        mindtraceMapper.insert(prototype);
        return SaResult.ok("Mindtrace data recorded: " + prototype.getId());
    }

    @Override
    public SaResult delete(Long userId, Long traceId) {
        Mindtrace mindtrace = mindtraceMapper.selectById(traceId);
        if(mindtrace == null)
            return SaResult.error("Mindtrace data not found: " + traceId);
        if(!mindtrace.getCreateBy().equals(userId))
            return SaResult.error("Authentication error: userId not matched " + userId);
        mindtraceMapper.deleteById(traceId);
        return SaResult.ok("Mindtrace data deleted: " + traceId);
    }

    @Override
    public SaResult modify(Long userId, Long traceId, MindtraceDTO dto) {
        Mindtrace mindtrace = mindtraceMapper.selectById(traceId);
        if(mindtrace == null)
            return SaResult.error("Mindtrace data not found: " + traceId);
        if(!mindtrace.getCreateBy().equals(userId))
            return SaResult.error("Authentication error: userId not matched " + userId);
        dto.setId(traceId);
        mindtraceMapper.updateById(Mindtrace.prototype(dto));
        return SaResult.ok("Mindtrace data updated: " + traceId);
    }

    @Override
    public Mindtrace check(Long userId, Long traceId) {
        Mindtrace mindtrace = mindtraceMapper.selectById(traceId);
        if(!mindtrace.getCreateBy().equals(userId)) return null;
        return mindtrace;
    }
}
