package pers.juumii.service;

import pers.juumii.data.EnhancerShare;
import pers.juumii.data.KnodeShare;
import pers.juumii.data.ResourceShare;

import java.util.List;

public interface QueryService {
    List<ResourceShare> getRelatedResourceShares(Long userId, Long knodeId);

    List<EnhancerShare> getRelatedEnhancerShares(Long userId, Long knodeId);

    List<KnodeShare> getRelatedKnodeShares(Long userId, Long knodeId);

}
