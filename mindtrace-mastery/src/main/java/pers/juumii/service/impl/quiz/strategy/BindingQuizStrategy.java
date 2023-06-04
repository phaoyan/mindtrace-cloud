package pers.juumii.service.impl.quiz.strategy;

import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.QuizStrategy;
import pers.juumii.service.QuizStrategyService;

import java.util.List;

@Service
public class BindingQuizStrategy implements QuizStrategyService {


    /**
     * strategy格式：
     * {
     *     resourceIds: number[] // strategy中绑定指定的resource
     * }
     */
    @Override
    public List<Long> getQuiz(QuizStrategy strategy) {
        List<String> resourceIds =
                QuizStrategyData.data(strategy.getQuizStrategy()).config()
                .getJSONArray("resourceIds").toList(String.class);
        return resourceIds.stream().map(Convert::toLong).toList();
    }

    @Override
    public Boolean canHandle(QuizStrategy strategy) {
        return QuizStrategyData.canHandle(strategy, QuizStrategyData.BINDING);
    }
}
