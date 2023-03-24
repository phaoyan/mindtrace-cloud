package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.utils.SaResult;

@Service
public interface KnodeService {

    SaResult branch(Long id, String title);

    SaResult branch(String stemTitle, String title);

    SaResult delete(Long id);

    SaResult delete(String title);

    SaResult check(String title);

    SaResult check(Long id);

    SaResult update(Long id, KnodeDTO dto);

    SaResult update(String title, KnodeDTO dto);

    SaResult label(Long id, String label);

    SaResult unlabel(Long id, String label);

    SaResult shift(Long stemId, Long branchId);

    SaResult connect(Long sourceId, Long targetId);
}
