package pers.juumii.service;

import pers.juumii.data.KnodeShare;
import pers.juumii.dto.share.KnodeShareDTO;

import java.util.List;

public interface KnodeShareService {
    List<KnodeShare> getRelatedKnodeShare(Long knodeId, Long count);
    List<KnodeShare> getRelatedKnodeShare(Long knodeId, Double threshold);

    KnodeShare getKnodeShare(Long knodeId);

    void updateKnodeShare(Long knodeId, KnodeShareDTO dto);

    void forkKnodeShare(Long shareId, Long targetId);
}
