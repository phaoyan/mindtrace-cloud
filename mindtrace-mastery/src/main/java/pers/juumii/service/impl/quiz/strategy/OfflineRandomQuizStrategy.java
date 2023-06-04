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
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfflineRandomQuizStrategy implements QuizStrategyService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;

    @Autowired
    public OfflineRandomQuizStrategy(
            CoreClient coreClient,
            EnhancerClient enhancerClient) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
    }

    @Override
    public List<Long> getQuiz(QuizStrategy strategy) {
        //提取参数
        QuizStrategyData data = QuizStrategyData.data(strategy.getQuizStrategy());
        Integer quizSize = Convert.toInt(
                Opt.ofNullable(data.getConfig().get(QuizStrategyParams.QUIZ_SIZE)).orElse(1));
        Integer backwardDepth = Convert.toInt(
                Opt.ofNullable(data.getConfig().get(QuizStrategyParams.BACKWARD_DEPTH)).orElse(3));

        List<Long> res = new ArrayList<>();
        List<KnodeDTO> ancestors = coreClient.ancestors(strategy.getKnodeId());
        for(KnodeDTO knode: ancestors.subList(0, Math.min(ancestors.size(), backwardDepth))){
            List<ResourceDTO> resources = enhancerClient.getResourcesOfKnode(Convert.toLong(knode.getId()));
            res.addAll(
                DataUtils.randomPick(resources, quizSize - res.size())
                .stream().map(resource->Convert.toLong(resource.getId()))
                .toList());
            if(quizSize >= res.size()) break;
        }
        return res;
    }

    @Override
    public Boolean canHandle(QuizStrategy strategy) {
        return QuizStrategyData.canHandle(strategy, QuizStrategyData.OFFLINE_RANDOM);
    }
}
