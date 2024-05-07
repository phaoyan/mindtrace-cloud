package pers.juumii.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.schedule.ScheduleTasks;
import pers.juumii.service.EnhancerService;
import pers.juumii.utils.Cypher;
import pers.juumii.utils.Neo4jUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StartingScripts implements ApplicationRunner {

    private final Neo4jUtils neo4j;
    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final EnhancerService enhancerService;
    private final CoreClient coreClient;
    private final ScheduleTasks scheduleTasks;

    public StartingScripts(
            Neo4jUtils neo4j,
            EnhancerKnodeRelationshipMapper ekrMapper,
            EnhancerService enhancerService, CoreClient coreClient,
            ScheduleTasks scheduleTasks) {
        this.neo4j = neo4j;
        this.ekrMapper = ekrMapper;
        this.enhancerService = enhancerService;
        this.coreClient = coreClient;
        this.scheduleTasks = scheduleTasks;
    }


    @Override
    public void run(ApplicationArguments args) {
        List<EnhancerKnodeRel> rels = ekrMapper.selectList(null);
        for(EnhancerKnodeRel rel: rels){
            Long enhancerId = rel.getEnhancerId();
            Long knodeId = rel.getKnodeId();
            if(Objects.isNull(coreClient.check(knodeId))) continue;
            Cypher query = Cypher.cypher("""
                    MATCH (knode: Knode {id:$knodeId})-[r:KNODE_TO_ENHANCER]->(enhancer: Enhancer {id: $enhancerId})
                    RETURN r
                    """, Map.of("knodeId", knodeId, "enhancerId", enhancerId));
            if(neo4j.session(query, (record)->record.get(0)).isEmpty())
                enhancerService.addKnodeEnhancerRel(knodeId, enhancerId);

        }
    }
}
