package pers.juumii.service.impl.v2;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.service.impl.v2.utils.Cypher;
import pers.juumii.service.impl.v2.utils.Neo4jUtils;
import pers.juumii.thread.ThreadUtils;

import java.util.List;
import java.util.Map;

@Service
public class KnodeServiceImplV2 implements KnodeService {

    private final Neo4jUtils neo4j;
    private final ThreadUtils threadUtils;
    private final KnodeQueryService knodeQuery;

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
                    knode.index = $index,
                    knode.isLeaf = $isLeaf
                """, Map.of(
                            "knodeId", knode.getId(),
                            "createBy", knode.getCreateBy(),
                            "createTime", knode.getCreateTime(),
                            "title", knode.getTitle(),
                            "index", knode.getIndex(),
                            "isLeaf", knode.getIsLeaf()
                ));
    }

    @Autowired
    public KnodeServiceImplV2(
            Neo4jUtils neo4j,
            ThreadUtils threadUtils,
            KnodeQueryService knodeQuery) {
        this.neo4j = neo4j;
        this.threadUtils = threadUtils;
        this.knodeQuery = knodeQuery;
    }

    @Override
    public Knode createRoot(Long userId) {
        Knode root = Knode.prototype("ROOT", null, userId);
        root.setIndex(0);
        threadUtils.getUserBlockingQueue().add(()->{
            neo4j.transaction(List.of(createBasic(root)));
        });
        return root;
    }

    @Override
    public Knode branch(Long userId, Long knodeId, String title) {
        Knode stem = knodeQuery.check(knodeId);
        Knode branch = Knode.sudoPrototype(title, knodeId, userId);
        branch.setIndex(stem.getBranches().size() - 1);
        threadUtils.getUserBlockingQueue().add(()->{
            neo4j.transaction(List.of(
                    createBasic(branch),
                    relateToStem(knodeId,branch.getId())));
        });
        return branch;
    }

    @Override
    public SaResult delete(Long knodeId) {
        Knode target = knodeQuery.check(knodeId);
        if(target.getBranches().isEmpty())
            threadUtils.getUserBlockingQueue().add(()->{
                neo4j.transaction(List.of(deleteBasic(knodeId)));
            });
        else throw new RuntimeException("Deleting knode failed: branches still exist.");
        return SaResult.ok();
    }

    @Override
    public SaResult update(Long knodeId, KnodeDTO dto) {
        threadUtils.getUserBlockingQueue().add(()->{
            Knode knode = knodeQuery.check(knodeId);
            Opt.ifPresent(dto.getCreateBy(), createBy->knode.setCreateBy(Convert.toLong(createBy)));
            Opt.ifPresent(dto.getCreateTime(), knode::setCreateTime);
            Opt.ifPresent(dto.getTitle(), knode::setTitle);
            Opt.ifPresent(dto.getIndex(), knode::setIndex);
            Opt.ifPresent(dto.getIsLeaf(), knode::setIsLeaf);
            neo4j.transaction(List.of(updateBasic(knode)));
        });
        return SaResult.ok();
    }

    @Override
    public SaResult addLabelToKnode(Long knodeId, String label) {
        return null;
    }

    @Override
    public SaResult removeLabelFromKnode(Long knodeId, String label) {
        return null;
    }

    @Override
    public List<Knode> shift(Long stemId, Long branchId, Long userId) {
        Cypher cypher = Cypher.cypher("""
                MATCH
                        (stem: Knode {id:$stemId}),
                        (branch: Knode {id:$branchId}),
                        (branch)-[st:STEM_FROM]->(ori),
                        (ori)-[br:BRANCH_TO]->(branch)
                DELETE st, br
                CREATE (branch)-[_st:STEM_FROM]->(stem)
                CREATE (stem)-[_br:BRANCH_TO]->(branch)
                
                """, Map.of("stemId", stemId, "branchId", branchId));

        neo4j.transaction(List.of(cypher));
        return knodeQuery.checkAll(userId);
    }

    @Override
    public SaResult connect(Long sourceId, Long targetId) {
        return null;
    }

    @Override
    public List<Knode> initIndex(Long userId) {
        return null;
    }

    @Override
    public void swapIndex(Long userId, Long stemId, Integer index1, Integer index2) {
        Cypher cypher = Cypher.cypher("""
                MATCH (stem: Knode {id:$stemId})-[:BRANCH_TO]->(branch1) WHERE branch1.index = $index1
                MATCH (stem: Knode {id:$stemId})-[:BRANCH_TO]->(branch2) WHERE branch2.index = $index2
                SET branch1.index = $index2,
                    branch2.index = $index1
                """, Map.of("stemId", stemId, "index1", index1, "index2", index2));
        threadUtils.getUserBlockingQueue().add(()->{
            neo4j.transaction(List.of(cypher));
        });
    }
}
