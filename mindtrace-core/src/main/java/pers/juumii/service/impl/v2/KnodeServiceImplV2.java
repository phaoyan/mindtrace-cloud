package pers.juumii.service.impl.v2;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.MqClient;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.utils.Cypher;
import pers.juumii.utils.Neo4jUtils;
import pers.juumii.thread.ThreadUtils;
import pers.juumii.utils.TimeUtils;

import java.util.List;
import java.util.Map;

@Service
public class KnodeServiceImplV2 implements KnodeService {

    private final Neo4jUtils neo4j;
    private final ThreadUtils threadUtils;
    private final KnodeQueryService knodeQuery;
    private final MqClient mqClient;

    public static Cypher createBasic(Knode knode){
        return Cypher.cypher("""
                CREATE (knode: Knode {
                    id: $knodeId,
                    createBy: $createBy,
                    createTime: $createTime,
                    title: $title,
                    index: $index,
                    isLeaf: false
                })
                """, Map.of(
                        "knodeId",knode.getId(),
                        "createBy",knode.getCreateBy(),
                        "createTime",knode.getCreateTime(),
                        "title",knode.getTitle(),
                        "index",knode.getIndex(),
                        "stemId",knode.getStem() != null ? knode.getStem().getId() : -1L
                ));
    }

    public static Cypher relateToStem(Long stemId, Long knodeId){
        return Cypher.cypher("""
                MATCH (stem: Knode {id:$stemId}), (knode: Knode {id:$knodeId})
                CREATE (stem)-[br:BRANCH_TO]->(knode)
                CREATE (knode)-[st:STEM_FROM]->(stem)
                """, Map.of("stemId",stemId,"knodeId",knodeId));
    }

    public static Cypher deleteBasic(Long knodeId){
        return Cypher.cypher("""
                MATCH (knode: Knode {id:$knodeId}) DETACH DELETE knode
                """, Map.of("knodeId", knodeId));
    }

    public static Cypher updateBasic(Knode knode){
        return Cypher.cypher("""
                MATCH (knode: Knode {id: $knodeId})
                SET knode.createBy = $createBy,
                    knode.createTime = $createTime,
                    knode.title = $title,
                    knode.index = $index
                """, Map.of(
                            "knodeId", knode.getId(),
                            "createBy", knode.getCreateBy(),
                            "createTime", knode.getCreateTime(),
                            "title", knode.getTitle(),
                            "index", knode.getIndex()
                ));
    }

    @Autowired
    public KnodeServiceImplV2(
            Neo4jUtils neo4j,
            ThreadUtils threadUtils,
            KnodeQueryService knodeQuery,
            MqClient mqClient) {
        this.neo4j = neo4j;
        this.threadUtils = threadUtils;
        this.knodeQuery = knodeQuery;
        this.mqClient = mqClient;
    }

    @Override
    public Knode branch(Long knodeId, String title) {
        // check里面有authentication所以不用重复了
        Knode stem = knodeQuery.check(knodeId);
        if(stem == null)
            throw new RuntimeException("Knode Not Found: " + knodeId);
        Knode branch = Knode.sudoPrototype(title, knodeId, stem.getCreateBy());
        branch.setIndex(stem.getBranches().size());
        threadUtils.getUserBlockingQueue().add(()->{
            neo4j.transaction(List.of(
                    createBasic(branch),
                    relateToStem(knodeId,branch.getId())));
        });
        mqClient.emit(MessageEvents.ADD_KNODE, JSONUtil.toJsonStr(Knode.transfer(branch)));
        return branch;
    }

    @Override
    public void delete(Long knodeId) {
        Knode target = knodeQuery.check(knodeId);
        if(target == null)
            throw new RuntimeException("Knode Not Found: " + knodeId);
        if(target.getStem() == null)
            throw new RuntimeException("Root Must Not Be Deleted: " + knodeId);
        if(target.getBranches().isEmpty()){
            threadUtils.getUserBlockingQueue().add(()-> neo4j.transaction(List.of(deleteBasic(knodeId))));
            mqClient.emit(MessageEvents.REMOVE_KNODE, JSONUtil.toJsonStr(Knode.transfer(target)));
        }

        else throw new RuntimeException("Deleting knode failed: branches still exist.");
    }

    @Override
    public void update(Long knodeId, KnodeDTO dto) {
        Knode knode = knodeQuery.check(knodeId);
        if(knode == null)
            throw new RuntimeException("Knode Not Found: " + knodeId);
        Opt.ifPresent(dto.getCreateBy(), createBy->knode.setCreateBy(Convert.toLong(createBy)));
        Opt.ifPresent(dto.getCreateTime(), (createTime)-> knode.setCreateTime(TimeUtils.parse(createTime)));
        Opt.ifPresent(dto.getTitle(), knode::setTitle);
        Opt.ifPresent(dto.getIndex(), knode::setIndex);
        threadUtils.getUserBlockingQueue().add(()-> neo4j.transaction(List.of(updateBasic(knode))));
        mqClient.emit(MessageEvents.UPDATE_KNODE, knode.getId().toString());
    }

    @Override
    public void editCreateTime(Long knodeId, String createTime) {
        KnodeDTO knodeDTO = new KnodeDTO();
        knodeDTO.setCreateTime(createTime);
        update(knodeId, knodeDTO);
    }

    @Override
    public void editCreateBy(Long knodeId, String createBy) {
        KnodeDTO knodeDTO = new KnodeDTO();
        knodeDTO.setCreateBy(createBy);
        update(knodeId, knodeDTO);
    }

    @Override
    public void editTitle(Long knodeId, String title) {
        KnodeDTO knodeDTO = new KnodeDTO();
        knodeDTO.setTitle(title);
        update(knodeId, knodeDTO);
    }

    @Override
    public void editIndex(Long knodeId, Integer index) {
        KnodeDTO knodeDTO = new KnodeDTO();
        knodeDTO.setIndex(index);
        update(knodeId, knodeDTO);
    }

    @Override
    public void connect(Long knodeId1, Long knodeId2) {
        Cypher cypher = Cypher.cypher("""
                MATCH (n1:Knode {id:$knodeId1})
                MATCH (n2:Knode {id:$knodeId2})
                CREATE (n1)-[r1:CONNECT_TO]->(n2)
                CREATE (n2)-[r2:CONNECT_TO]->(n1)
                """, Map.of("knodeId1", knodeId1, "knodeId2", knodeId2));
        neo4j.transaction(List.of(cypher));
    }

    @Override
    public void disconnect(Long knodeId1, Long knodeId2) {
        Cypher cypher = Cypher.cypher("""
                MATCH (n1:Knode {id:$knodeId1})-[r1:CONNECT_TO]->(n2:Knode {id:$knodeId2})
                MATCH (n2)-[r2:CONNECT_TO]->(n1)
                DELETE r1, r2
                """, Map.of("knodeId1", knodeId1, "knodeId2", knodeId2));
        neo4j.transaction(List.of(cypher));
    }

    @Override
    public void shift(Long stemId, Long branchId) {
        // userId用于鉴权
        Cypher cypher = Cypher.cypher("""
                MATCH
                        (stem: Knode {id:$stemId, createBy:$userId}),
                        (branch: Knode {id:$branchId, createBy:$userId}),
                        (branch)-[st:STEM_FROM]->(ori),
                        (ori)-[br:BRANCH_TO]->(branch)
                DELETE st, br
                CREATE (branch)-[_st:STEM_FROM]->(stem)
                CREATE (stem)-[_br:BRANCH_TO]->(branch)
                
                """, Map.of(
                "stemId", stemId,
                "branchId", branchId,
                "userId", StpUtil.getLoginIdAsLong()));
        neo4j.transaction(List.of(cypher));
    }

    @Override
    public void swapIndex(Long stemId, Integer index1, Integer index2) {
        // 检测index是否合法
        List<Knode> branches = knodeQuery.check(stemId).getBranches();
        for(int i = 0; i < branches.size(); i ++)
            if(!branches.stream().map(Knode::getIndex).toList().contains(i)){
                for(int j = 0; j < branches.size(); j ++){
                    KnodeDTO dto = new KnodeDTO();
                    dto.setIndex(j);
                    update(branches.get(j).getId(), dto);
                }
                return;
            }

        // userId用于鉴权
        Cypher cypher = Cypher.cypher("""
                MATCH (stem: Knode {id:$stemId, createBy:$userId})-[:BRANCH_TO]->(branch1) WHERE branch1.index = $index1
                MATCH (stem: Knode {id:$stemId, createBy:$userId})-[:BRANCH_TO]->(branch2) WHERE branch2.index = $index2
                SET branch1.index = $index2,
                    branch2.index = $index1
                """, Map.of(
                "stemId", stemId,
                "userId", StpUtil.getLoginIdAsLong(),
                "index1", index1,
                "index2", index2));
        threadUtils.getUserBlockingQueue().add(()-> neo4j.transaction(List.of(cypher)));
    }




}
