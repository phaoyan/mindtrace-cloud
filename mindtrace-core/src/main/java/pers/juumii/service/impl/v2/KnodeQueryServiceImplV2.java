package pers.juumii.service.impl.v2;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.InternalNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.data.Label;
import pers.juumii.dto.EmbeddingVectorDTO;
import pers.juumii.feign.VectorClient;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.UserService;
import pers.juumii.utils.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class KnodeQueryServiceImplV2 implements KnodeQueryService {

    private final Neo4jUtils neo4j;
    private final UserService userService;
    private final VectorClient vectorClient;

    public static Cypher shallowLink(){
        return Cypher.cypher("""
                OPTIONAL MATCH (knode)-[tag:TAG]->(label)
                OPTIONAL MATCH (knode)-[br:BRANCH_TO]->(branch)
                OPTIONAL MATCH (knode)-[st:STEM_FROM]->(stem)
                OPTIONAL MATCH (knode)-[con:CONNECT_TO]->(connection)
                """, Map.of());
    }

    public static Cypher basicReturn(){
        return Cypher.cypher("""
                RETURN
                        knode AS knode,
                        collect(label) AS labels,
                        stem AS stem,
                        collect(branch) AS branches,
                        collect(connection) AS connections
                """, Map.of());
    }

    public static Function<Record, Knode> knodeResolver = (record)->{
        Map<String, Object> knode = record.get("knode").asMap();
        Value stemValue = record.get("stem");
        Map<String, Object> stem = stemValue.isNull() ? null : stemValue.asMap();
        List<Object> branches = record.get("branches").asList();
        List<Object> labels = record.get("labels").asList();
        List<Object> connections = record.get("connections").asList();

        Knode _knode = Knode.prototype(knode);
        Knode _stem = stem == null ? null : Knode.prototype(stem);
        List<Knode> _branches = branches.stream().map(br->Knode.prototype(((InternalNode) br).asMap())).toList();
        List<Knode> _connections = connections.stream().map(conn->Knode.prototype((((InternalNode) conn).asMap()))).toList();
        List<Label> _labels = labels.stream().map(l->Label.prototype(((InternalNode) l).asMap())).toList();

        _knode.setStem(_stem);
        _knode.setBranches(_branches);
        _knode.setConnections(_connections);
        _knode.setLabels(_labels);

        return _knode;
    };

    @Autowired
    public KnodeQueryServiceImplV2(Neo4jUtils neo4j, UserService userService, VectorClient vectorClient) {
        this.neo4j = neo4j;
        this.userService = userService;
        this.vectorClient = vectorClient;
    }

    @Override
    public Knode check(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("MATCH (knode:Knode {id: $knodeId})", Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        List<Knode> res = neo4j.session(cypher, knodeResolver);
        if(res.isEmpty()) return null;
        return res.get(0);
    }

    @Override
    public List<Knode> checkByLike(String like, Integer count) {
        if(StrUtil.isBlank(like)) return new ArrayList<>();
        Cypher cypher = Cypher
                .cypher("MATCH (knode:Knode) WHERE knode.title =~ '.*"+like+".*'", Map.of())
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Knode> checkByDate(String left, String right, Long knodeId) {
        if(right == null)
            right = TimeUtils.format(LocalDateTime.now());
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        UNWIND nodes AS knode
                        MATCH (knode) WHERE 
                        knode.createTime >= $left AND 
                        knode.createTime <= $right
                        """,
                        Map.of("left", left, "right", right))
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Knode> checkAllRoots() {
        Cypher cypher = Cypher.cypher("""
                        MATCH (u: User)-[:POSSESS]->(knode: Knode)
                        """, Map.of())
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Knode> checkByLabel(String labelName) {
        return null;
    }

    @Override
    public Knode checkFully(Long knodeId) {
        return null;
    }

    @Override
    public List<Knode> checkByTitle(Long userId, String title) {
        return null;
    }

    @Override
    public List<Knode> branches(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (ori)-[:BRANCH_TO]->(knode) WHERE ori.id=$knodeId
                        """, Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Knode> leaves(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        WITH nodes, [node IN nodes WHERE node.createBy=n.createBy AND NOT (node)-[:BRANCH_TO]->()] AS leafNodes
                        UNWIND leafNodes AS knode
                        """, Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public Integer leaveCount(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        WITH nodes, [node IN nodes WHERE node.createBy=n.createBy AND NOT (node)-[:BRANCH_TO]->()] AS leafNodes
                        UNWIND leafNodes AS knode
                        RETURN COUNT(knode)
                        """, Map.of("knodeId", knodeId));
        return neo4j.session(cypher, record -> record.get("COUNT(knode)").asInt()).get(0);
    }

    @Override
    public Knode stem(Long knodeId) {
        return check(check(knodeId).getStem().getId());
    }

    @Override
    public List<Knode> ancestors(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (target: Knode {id:$knodeId})-[:STEM_FROM*0..]->(knode: Knode)
                        """, Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        return DataUtils.reverse(neo4j.session(cypher, knodeResolver));
    }

    @Override
    public Map<Long, List<Long>> ancestorIdsBatch(List<Long> knodeIds) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (knode: Knode) WHERE knode.id IN $knodeIds
                        CALL {
                            WITH knode
                            MATCH (knode)-[:STEM_FROM*0..]->(anc)
                            RETURN COLLECT(anc.id) AS ancIds
                        }
                        RETURN COLLECT(ancIds) AS ancSeriesList
                        """, Map.of("knodeIds", knodeIds));
        List<List<Long>> ancSeriesList = neo4j.session(cypher,
                record -> record.get(0).asList(v -> v.asList(Value::asLong)))
                .get(0);
        HashMap<Long, List<Long>> res = new HashMap<>();
        for(List<Long> ancSeries: ancSeriesList)
            res.put(ancSeries.get(0), ancSeries);
        return res;
    }

    @Override
    public List<Knode> knodeChain(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (target: Knode {id:$knodeId})-[:STEM_FROM*0..]->(knode: Knode)
                        """, Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public Knode findRoot(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                    MATCH (u:User {id:$userId})-[:POSSESS]->(knode)
                    """, Map.of("userId", check(knodeId).getCreateBy()))
                .append(shallowLink())
                .append(basicReturn());
        List<Knode> res = neo4j.session(cypher, knodeResolver);
        if(res.isEmpty())
            throw new RuntimeException("Root finding failed: Knode not found " + knodeId);
        return res.get(0);
    }

    @Override
    public List<String> chainStyleTitle(Long knodeId) {
        return knodeChain(knodeId).stream().map(Knode::getTitle).toList();
    }

    /**
     * 返回值的键是string knode id
     */
    @Override
    public Map<String, List<String>> chainStyleTitleBeneath(Long knodeId) {
        HashMap<String, List<String>> res = new HashMap<>();
        Function<Record, List<String>> resolver = (record)->{
            long id = record.get("knodeId").asLong();
            List<String> chainStyleTitle = record.get("chainStyleTitle").asList(Value::asString);
            List<String> _res = new ArrayList<>();
            _res.add(Long.toString(id));
            _res.addAll(chainStyleTitle);
            return _res;
        };
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        UNWIND nodes AS knode
                        CALL {
                            WITH knode
                            MATCH (knode)-[:STEM_FROM*0..]->(anc: Knode)
                            RETURN collect(anc.title) AS chainStyleTitle
                        }
                        RETURN knode.id AS knodeId, chainStyleTitle
                        """, Map.of("knodeId", knodeId));
        List<List<String>> results = neo4j.session(cypher, resolver);
        for(List<String> result: results)
            res.put(result.get(0), result.subList(1, result.size()));
        return res;
    }

    @Override
    public Boolean isOffspring(Long childId, Long parentId) {
        return knodeChain(childId).stream().map(Knode::getId).toList().contains(parentId);
    }

    @Override
    public List<Knode> offsprings(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        WITH nodes as all
                        UNWIND all AS knode
                        """, Map.of("knodeId", knodeId))
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Long> offspringIds(Long knodeId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (n:Knode {id: $knodeId})
                        CALL apoc.path.subgraphAll(n, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        WITH nodes as all
                        UNWIND all AS knode
                        RETURN knode.id
                        """, Map.of("knodeId", knodeId));
        return neo4j.session(cypher, record -> record.get(0).asLong());
    }

    @Override
    public List<Knode> checkBySimilar(String txt, Double threshold) {
        List<EmbeddingVectorDTO> dataList = vectorClient.searchSimilar(txt, "knode", threshold);
        return dataList.stream()
                .sorted(Comparator.comparing(EmbeddingVectorDTO::getScore).reversed())
                .map(data -> check(Convert.toLong(data.getId())))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void resetKnodeVectorBase() {
        try {
            List<Knode> knodes = checkAll();
            List<JSONObject> data = knodes.stream()
                    .map(Knode::getId)
                    .filter(Objects::nonNull)
                    .map(knodeId -> JSONUtil.createObj()
                            .set("id", knodeId)
                            .set("txt", StringUtils.spacedTexts(chainStyleTitle(knodeId))))
                    .toList();
            JSONObject params = JSONUtil.createObj()
                    .set("data", data)
                    .set("vector-base", "knode");
            vectorClient.resetVectorBase(JSONUtil.toJsonStr(params));
        }catch (Exception ignored){}
    }

    @Override
    public List<Knode> checkAll(Long userId) {
        Cypher cypher = Cypher
                .cypher("""
                        MATCH (u: User {id:$userId})-[:POSSESS]->(root: Knode)
                        CALL apoc.path.subgraphAll(root, {relationshipFilter: 'BRANCH_TO>'}) YIELD nodes
                        WITH nodes as all
                        UNWIND all AS knode
                        """, Map.of("userId", userId))
                .append(shallowLink())
                .append(basicReturn());
        List<Knode> res = neo4j.session(cypher, knodeResolver);
        if(res.isEmpty())
            userService.register(userId);
        return neo4j.session(cypher, knodeResolver);
    }

    @Override
    public List<Knode> checkAll() {
        Cypher cypher = Cypher.cypher("""
                        MATCH (knode: Knode)
                        """, Map.of())
                .append(shallowLink())
                .append(basicReturn());
        return neo4j.session(cypher, knodeResolver);
    }


}