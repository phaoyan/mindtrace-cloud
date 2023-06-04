package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;

import java.util.List;

@Service
public interface EnhancerService {


    List<Enhancer> getAllEnhancers(Long userId);

    List<Enhancer> getEnhancersFromKnode(Long knodeId);

    Enhancer getEnhancerById(Long enhancerId);

    Enhancer addEnhancerToUser(Long userId);

    Enhancer addEnhancerToKnode(Long knodeId);

    SaResult updateEnhancer(Long enhancerId, EnhancerDTO updated);

    void removeEnhancer(Long enhancerId);

    void connectEnhancerToKnode(Long knodeId, Long enhancerId);

    void disconnectEnhancerFromKnode(Long knodeId, Long enhancerId);

    List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId);
}
