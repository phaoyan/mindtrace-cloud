package pers.juumii.service.impl.quiz.strategy;

import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.QuizStrategy;
import pers.juumii.service.QuizStrategyService;

import java.util.List;

@Service
public class OnlineRandomQuizStrategy implements QuizStrategyService {
    @Override
    public List<Long> getQuiz(QuizStrategy strategy) {
        return null;
    }

    @Override
    public Boolean canHandle(QuizStrategy strategy) {
        return false;
    }
}
