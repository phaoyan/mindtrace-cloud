package pers.juumii.service;


import pers.juumii.data.EnhancerShare;
import pers.juumii.dto.enhancer.EnhancerDTO;

import java.util.List;

public interface EnhancerShareService {
    List<EnhancerShare> getOwnedEnhancerShare(Long knodeId);

    EnhancerDTO forkEnhancerShare(Long shareId, Long targetId);

    EnhancerShare getEnhancerShare(Long enhancerId);
}
