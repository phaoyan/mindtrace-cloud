package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.service.TraceEnhancerRelService;

@Service
public class TraceEnhancerRelServiceImpl implements TraceEnhancerRelService {

    private final TraceEnhancerRelMapper traceEnhancerRelMapper;

    @Autowired
    public TraceEnhancerRelServiceImpl(TraceEnhancerRelMapper traceEnhancerRelMapper) {
        this.traceEnhancerRelMapper = traceEnhancerRelMapper;
    }

    @Override
    public void postEnhancerTraceRel(Long traceId, Long enhancerId) {
        traceEnhancerRelMapper.insert(TraceEnhancerRel.prototype(traceId, enhancerId));
    }


}
