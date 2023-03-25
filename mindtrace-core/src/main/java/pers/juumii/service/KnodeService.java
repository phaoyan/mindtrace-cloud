package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;

@Service
public interface KnodeService {

    Knode branch(Long knodeId, String title);

    SaResult delete(Long knodeId);

    SaResult update(Long knodeId, KnodeDTO dto);

    SaResult label(Long knodeId, String label);

    SaResult unlabel(Long knodeId, String label);

    SaResult shift(Long stemId, Long branchId);

    SaResult connect(Long sourceId, Long targetId);
}
