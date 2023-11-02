package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;

import java.util.List;

@Service
public interface EnhancerService {


    List<Enhancer> getAllEnhancers(Long userId);

    List<Enhancer> getEnhancersFromKnode(Long knodeId);

    Enhancer getEnhancerById(Long enhancerId);

    Enhancer addEnhancerToUser(Long userId);

    Enhancer addEnhancerToKnode(Long knodeId);

    void updateEnhancer(Long enhancerId, EnhancerDTO updated);

    void setIsQuiz(Long enhancerId, Boolean isQuiz);

    void setTitle(Long enhancerId, String title);

    void removeEnhancer(Long enhancerId);

    void connectEnhancerToKnode(Long knodeId, Long enhancerId);

    void disconnectEnhancerFromKnode(Long knodeId, Long enhancerId);

    List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId);

    List<Long> getEnhancerIdsFromKnodeIncludingBeneath(Long knodeId);

    Long getEnhancerCount(Long knodeId);

    List<Enhancer> getEnhancersFromKnodeBatch(List<Long> knodeIds);

    List<KnodeDTO> getKnodeByEnhancerId(Long enhancerId);

    List<IdPair> getKnodeEnhancerRels(List<Long> knodeIds);

    List<Enhancer> getEnhancersByDate(Long userId, String left, String right);

    List<Enhancer> getEnhancersByDateBeneathKnode(Long knodeId, String left, String right);

    List<Long> getKnodeIdsWithQuiz(Long rootId);
}
