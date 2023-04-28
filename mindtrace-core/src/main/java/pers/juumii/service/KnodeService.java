package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.neo4j.cypher.internal.expressions.In;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;

import java.util.List;

@Service
public interface KnodeService {

    Knode createRoot(Long userId);

    Knode branch(Long userId, Long knodeId, String title);

    SaResult delete(Long knodeId);

    SaResult update(Long knodeId, KnodeDTO dto);

    SaResult addLabelToKnode(Long knodeId, String label);

    SaResult removeLabelFromKnode(Long knodeId, String label);

    List<Knode> shift(Long stemId, Long branchId, Long userId);

    SaResult connect(Long sourceId, Long targetId);

    List<Knode> initIndex(Long userId);

    void swapIndex(Long userId, Long stemId, Integer index1, Integer index2);
}
