package pers.juumii.service.impl;

import cn.hutool.core.collection.ListUtil;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.MapAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.data.Label;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.UserService;
import pers.juumii.utils.DataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class KnodeQueryServiceImpl implements KnodeQueryService {

    private final KnodeRepository knodeRepo;
    private final Neo4jClient client;

    private UserService userService;

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public KnodeQueryServiceImpl(KnodeRepository knodeRepo, Neo4jClient client) {
        this.knodeRepo = knodeRepo;
        this.client = client;
    }

    @Override
    public Knode check(Long knodeId) {
        // 存在性在Aspect中已经检验
        return knodeRepo.findById(knodeId).get();
    }

    @Override
    public Knode checkFully(Long knodeId) {
        // TODO

        return null;
    }

    @Override
    public List<Knode> checkByTitle(Long userId, String title) {
        String cypher = """
            MATCH (user:User)-[:POSSESS]->(root)-[:BRANCH_TO]->(knode) WHERE user.id=$userId AND knode.title=$title
            """;
        Map<String, Object> params = Map.of("userId", userId, "title", title);
        return wrappedQuery(cypher, params);
    }

    @Override
    public List<Knode> branches(Long knodeId) {
        String cypher = """
                MATCH (ori)-[:BRANCH_TO]->(knode) WHERE ori.id=$knodeId
                """;
        return wrappedQuery(cypher,Map.of("knodeId", knodeId));
    }

    @Override
    public List<Knode> offsprings(Long knodeId) {
        String cypher = """
                MATCH (root)-[:BRANCH_TO*]->(knode) WHERE root.id=$knodeId
                """;
        return wrappedQuery(cypher, Map.of("knodeId", knodeId));
    }

    @Override
    public List<Knode> leaves(Long knodeId) {
        String cypher = """
                MATCH (knode)-[:STEM_FROM*]->(ancestor) WHERE ancestor.id=$knodeId
                MATCH (knode) WHERE NOT EXISTS {
                    MATCH (knode)-[:BRANCH_TO]->(other)
                    RETURN other
                }
                """;
        return wrappedQuery(cypher, Map.of("knodeId", knodeId));
    }

    @Override
    public Knode stem(Long knodeId) {
        String cypher = """
                MATCH (branch)-[:STEM_FROM]->(knode) WHERE branch.id=$knodeId
                """;
        List<Knode> res = wrappedQuery(cypher, Map.of("knodeId", knodeId));
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public List<Knode> ancestors(Long knodeId) {
        String cypher = """
                MATCH (child)-[:STEM_FROM*]->(knode) WHERE child.id=$knodeId
                """;
        return wrappedQuery(cypher, Map.of("knodeId", knodeId));
    }

    @Override
    public List<Knode> knodeChain(Long knodeId) {
        List<Knode> ancestors = ancestors(knodeId);
        ancestors.add(check(knodeId));
        return ancestors;
    }

    @Override
    public Knode findRoot(Long knodeId) {
        List<Knode> ancestors = ancestors(knodeId);
        if(ancestors.isEmpty()) return check(knodeId);
        return ancestors.get(ancestors.size()-1);
    }

    @Override
    public List<Knode> checkByLabel(String labelName) {
        // TODO
        return null;
    }

    @Override
    public String chainStyleTitle(Long knodeId) {
        StringBuilder res = new StringBuilder();
        for (Knode knode: ancestors(knodeId))
            res.append(knode.getTitle()).append(".");
        res.append(check(knodeId).getTitle());
        return res.toString();
    }

    @Override
    public List<Knode> checkAll(Long userId) {
        Long rootId = userService.checkRootId(userId);
        return DataUtils.join(offsprings(rootId), check(rootId));
    }

    private String cypherBasic(String raw){
        return raw + """
                OPTIONAL MATCH (knode)-[tag:TAG]->(label)
                OPTIONAL MATCH (knode)-[br:BRANCH_TO]->(branch)
                OPTIONAL MATCH (knode)-[st:STEM_FROM]->(stem)
                OPTIONAL MATCH (knode)-[con:CONNECT_TO]->(connection)
                RETURN  knode AS knode,
                        collect(label) AS labels,
                        stem AS stem,
                        collect(branch) AS branches,
                        collect(connection) AS connections
                """;
    }

    private List<Knode> query(String cypher, Map<String, Object> params){
        Collection<Knode> all = client.query(cypher).bindAll(params).fetchAs(Knode.class).mappedBy(((typeSystem, record) -> {
            if(record.get("knode").isNull())
                return null;
            Map<String, Object> knode = record.get("knode").asEntity().asMap();
            Knode res = Knode.prototype(knode);
            if(!record.get("stem").isNull())
                res.setStem(Knode.prototype(record.get("stem").asEntity().asMap()));
            List<Map<String, Object>> branches = record.get("branches").asList(Value::asEntity).stream().map(MapAccessor::asMap).toList();
            res.setBranches(branches.stream().map(Knode::prototype).toList());
            List<Map<String, Object>> connections = record.get("connections").asList(Value::asEntity).stream().map(MapAccessor::asMap).toList();
            res.setConnections(connections.stream().map(Knode::prototype).toList());
            List<Map<String, Object>> labels = record.get("labels").asList(Value::asEntity).stream().map(MapAccessor::asMap).toList();
            res.setLabels(labels.stream().map(Label::prototype).toList());
            return res;
        })).all();
        return ListUtil.toList(all);
    }

    private List<Knode> wrappedQuery(String raw, Map<String, Object> params){
        return query(cypherBasic(raw), params);
    }

}
