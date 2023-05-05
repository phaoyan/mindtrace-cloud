package pers.juumii.service;


import pers.juumii.data.EnhancerShare;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.share.EnhancerShareDTO;

import java.util.List;
import java.util.Map;

public interface EnhancerShareService {
    List<EnhancerShare> getOwnedEnhancerShare(Long knodeId);
    List<EnhancerShare> getRelatedEnhancerShare(Long knodeId, Long count);
    Map<String, List<EnhancerShareDTO>> getRelatedEnhancerShareWithMapping(Long knodeId, Long knodeCount);

    EnhancerDTO forkEnhancerShare(Long shareId, Long targetId);
}
