package pers.juumii.service.impl;

import org.springframework.stereotype.Service;
import pers.juumii.data.EnhancerShare;
import pers.juumii.data.KnodeShare;
import pers.juumii.data.ResourceShare;
import pers.juumii.service.QueryService;

import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {
    @Override
    public List<ResourceShare> getRelatedResourceShares(Long userId, Long knodeId) {
        return null;
    }

    @Override
    public List<EnhancerShare> getRelatedEnhancerShares(Long userId, Long knodeId) {
        return null;
    }

    @Override
    public List<KnodeShare> getRelatedKnodeShares(Long userId, Long knodeId) {
        return null;
    }
}
