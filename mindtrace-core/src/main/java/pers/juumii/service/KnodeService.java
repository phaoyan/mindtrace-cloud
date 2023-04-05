package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;

@Service
public interface KnodeService {

    Knode branch(Long knodeId, String title);

    SaResult delete(Long knodeId);

    SaResult clear(Long userId);

    SaResult update(Long knodeId, KnodeDTO dto);

    SaResult addLabelToKnode(Long knodeId, String label);

    SaResult removeLabelFromKnode(Long knodeId, String label);

    SaResult shift(Long stemId, Long branchId);

    SaResult connect(Long sourceId, Long targetId);

}
