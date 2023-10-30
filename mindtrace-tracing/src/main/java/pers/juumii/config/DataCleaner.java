package pers.juumii.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.data.persistent.TraceKnodeRel;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.service.StudyTraceService;

import java.util.List;

//@Component
public class DataCleaner implements ApplicationRunner {

    private final StudyTraceService service;
    private final EnhancerClient enhancerClient;
    private final CoreClient coreClient;

    @Autowired
    public DataCleaner(
            StudyTraceService service,
            EnhancerClient enhancerClient,
            CoreClient coreClient) {
        this.service = service;
        this.enhancerClient = enhancerClient;
        this.coreClient = coreClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<TraceEnhancerRel> ters = service.getAllTraceEnhancerRels();
        List<TraceKnodeRel> tkrs = service.getAllTraceKnodeRels();
        for(TraceEnhancerRel rel: ters)
            if(enhancerClient.getEnhancerById(rel.getEnhancerId()) == null)
                service.removeTraceEnhancerRel(rel.getId());
        for (TraceKnodeRel rel: tkrs)
            if(coreClient.check(rel.getKnodeId()) == null)
                service.removeTraceKnodeRel(rel.getId());
    }
}
