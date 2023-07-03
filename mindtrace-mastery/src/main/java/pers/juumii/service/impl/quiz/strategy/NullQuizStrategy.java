package pers.juumii.service.impl.quiz.strategy;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.QuizStrategy;
import pers.juumii.service.QuizStrategyService;
import pers.juumii.utils.DesignPatternUtils;

import java.util.List;

@Service
public class NullQuizStrategy implements QuizStrategyService {

    public static final String DEFAULT_STRATEGY = """
            {
                "type":"offline type priority",
                "config":{
                    "priority":["quizcard", "cloze", "markdown"],
                    "quizSize":1,
                    backwardDepth:3
                }
            }
            """;

    @Override
    public List<Long> getQuiz(QuizStrategy strategy) {
        strategy.setQuizStrategy(DEFAULT_STRATEGY);
        return DesignPatternUtils.route(QuizStrategyService.class, st->st.canHandle(strategy))
                .getQuiz(strategy);
    }

    @Override
    public Boolean canHandle(QuizStrategy strategy) {
        return false;
    }
}
