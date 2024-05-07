package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.enhancer.EnhancerDTO;
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

    void addKnodeEnhancerRel(Long knodeId, Long enhancerId);

    void removeKnodeEnhancerRel(Long knodeId, Long enhancerId);

    List<Enhancer> getEnhancersFromKnodeIncludingBeneath(Long knodeId);

    List<Long> getEnhancerIdsFromKnodeIncludingBeneath(Long knodeId);

    Long getEnhancerCount(Long knodeId);

    List<Enhancer> getEnhancersFromKnodeBatch(List<Long> knodeIds);

    List<KnodeDTO> getKnodeByEnhancerId(Long enhancerId);

    List<IdPair> getKnodeEnhancerRels(List<Long> knodeIds);

    List<Enhancer> getEnhancersByDate(Long userId, String left, String right);

    List<Enhancer> getEnhancersByDateBeneathKnode(Long knodeId, String left, String right);

    List<Long> getKnodeIdsWithQuiz(Long rootId);

    void setEnhancerIndexInKnode(Long knodeId, Long enhancerId, Integer index);

    List<Enhancer> getEnhancersByResourceId(Long resourceId);

    List<Enhancer> getEnhancersByLike(Long userId, String txt);
}
