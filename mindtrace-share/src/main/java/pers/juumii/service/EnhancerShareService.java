package pers.juumii.service;


import pers.juumii.data.EnhancerShare;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.share.EnhancerShareDTO;

import java.util.List;
import java.util.Map;

public interface EnhancerShareService {
    List<EnhancerShare> getOwnedEnhancerShare(Long knodeId);

    EnhancerDTO forkEnhancerShare(Long shareId, Long targetId);

    EnhancerShare getEnhancerShare(Long enhancerId);
}
