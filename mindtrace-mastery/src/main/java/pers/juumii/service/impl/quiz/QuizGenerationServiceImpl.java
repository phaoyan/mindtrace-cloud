package pers.juumii.service.impl.quiz;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.enhancer.ResourceDTO;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.service.QuizGenerationService;

import java.util.List;

@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final EnhancerClient enhancerClient;

    @Autowired
    public QuizGenerationServiceImpl(EnhancerClient enhancerClient) {
        this.enhancerClient = enhancerClient;
    }

    @Override
    public List<Long> getQuiz(Long knodeId) {
        List<EnhancerDTO> enhancers = enhancerClient.getEnhancersOfKnode(knodeId);
        List<Long> quizEnhancerIds = enhancers.stream()
                .filter(EnhancerDTO::getIsQuiz)
                .map(enhancer -> Convert.toLong(enhancer.getId()))
                .toList();
        List<ResourceDTO> quizResources = enhancerClient.getResourcesOfEnhancerBatch(quizEnhancerIds);
        return quizResources.stream().map(resource->Convert.toLong(resource.getId())).toList();
    }

}
