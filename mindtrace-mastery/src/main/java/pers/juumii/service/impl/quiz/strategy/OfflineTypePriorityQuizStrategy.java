package pers.juumii.service.impl.quiz.strategy;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.QuizStrategy;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.service.QuizStrategyService;
import pers.juumii.utils.SerialTimer;

import java.util.*;

@Service
public class OfflineTypePriorityQuizStrategy implements QuizStrategyService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;

    @Autowired
    public OfflineTypePriorityQuizStrategy(
            CoreClient coreClient,
            EnhancerClient enhancerClient) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
    }

    @Override
    public List<Long> getQuiz(QuizStrategy strategy) {
        if(strategy.getKnodeId() == null) return new ArrayList<>();
        // 提取参数
        QuizStrategyData data = QuizStrategyData.data(strategy.getQuizStrategy());
        List<String> priority = Convert.toList(String.class,
                Opt.ofNullable(data.getConfig().get(QuizStrategyParams.PRIORITY)).orElse(new ArrayList<>()));
        Integer quizSize = Convert.toInt(
                Opt.ofNullable(data.getConfig().get(QuizStrategyParams.QUIZ_SIZE)).orElse(1));
        Integer backwardDepth = Convert.toInt(
                Opt.ofNullable(data.getConfig().get(QuizStrategyParams.BACKWARD_DEPTH)).orElse(3));

        List<Long> res = new ArrayList<>();
        List<KnodeDTO> ancestors = coreClient.ancestors(strategy.getKnodeId());

        for(KnodeDTO knode: ancestors.subList(0,Math.min(ancestors.size(), backwardDepth))){
            // 按照resource type给涉及的resource分类，然后按照priority中的优先级给这些type分优先级
            Map<String, List<Long>> resourceTypeQuizMapping = new HashMap<>();
            List<ResourceDTO> resources = enhancerClient.getResourcesOfKnode(Convert.toLong(knode.getId()));
            for(ResourceDTO resource: resources){
                if(!resourceTypeQuizMapping.containsKey(resource.getType()))
                    resourceTypeQuizMapping.put(resource.getType(), new ArrayList<>());
                resourceTypeQuizMapping.get(resource.getType()).add(Convert.toLong(resource.getId()));
            }

            // 按照优先级对搜索到的所有resource排序放在priorityList中
            List<Long> priorityList = new ArrayList<>();
            for(String type: priority){
                if(resourceTypeQuizMapping.containsKey(type))
                    priorityList.addAll(resourceTypeQuizMapping.get(type));
                resourceTypeQuizMapping.remove(type);
            }

            // 没有被priority记录的resource type挂载到priorityList的末尾
//            for(List<Long> rest: resourceTypeQuizMapping.values())
//                priorityList.addAll(rest);

            if(!priorityList.isEmpty())
                res.addAll(priorityList.subList(0, quizSize-res.size()));
            if(res.size() >= quizSize) break;
        }

        return res;
    }

    @Override
    public Boolean canHandle(QuizStrategy strategy) {
        return QuizStrategyData.canHandle(strategy, QuizStrategyData.OFFLINE_TYPE_PRIORITY);
    }
}
