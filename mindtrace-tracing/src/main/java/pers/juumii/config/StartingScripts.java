package pers.juumii.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pers.juumii.data.persistent.MilestoneTraceRel;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceEnhancerRel;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.MilestoneTraceRelMapper;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceEnhancerRelMapper;
import pers.juumii.service.StudyTraceService;
import pers.juumii.utils.Cypher;
import pers.juumii.utils.Neo4jUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StartingScripts implements ApplicationRunner {

    private final MilestoneTraceRelMapper relMapper;
    private final StudyTraceMapper studyTraceMapper;
    private final StudyTraceService studyTraceService;
    private final TraceEnhancerRelMapper terMapper;
    private final EnhancerClient enhancerClient;
    private final Neo4jUtils neo4j;

    public StartingScripts(
            MilestoneTraceRelMapper mapper,
            StudyTraceMapper studyTraceMapper,
            StudyTraceService studyTraceService,
            TraceEnhancerRelMapper terMapper,
            EnhancerClient enhancerClient,
            Neo4jUtils neo4j) {
        this.relMapper = mapper;
        this.studyTraceMapper = studyTraceMapper;
        this.studyTraceService = studyTraceService;
        this.terMapper = terMapper;
        this.enhancerClient = enhancerClient;
        this.neo4j = neo4j;
    }


    @Override
    public void run(ApplicationArguments args) {
        List<TraceEnhancerRel> teRels = terMapper.selectList(null);
        for(TraceEnhancerRel rel: teRels){
            Long enhancerId = rel.getEnhancerId();
            Long traceId = rel.getTraceId();
            if(Objects.isNull(enhancerClient.getEnhancerById(enhancerId))) continue;
            Cypher cypher = Cypher.cypher("""
                    MATCH (trace: StudyTrace {id: $traceId})-[r:TRACE_TO_ENHANCER]->(enhancer: Enhancer {id: $enhancerId})
                    RETURN r
                    """, Map.of("traceId", traceId, "enhancerId", enhancerId));
            if(neo4j.session(cypher, (record)->record.get(0)).isEmpty())
                studyTraceService.addTraceEnhancerRel(traceId, enhancerId);
        }

    }
}
