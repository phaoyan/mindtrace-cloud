package pers.juumii.service;

import pers.juumii.service.impl.quiz.strategy.QuizStrategyData;

import java.util.List;

public interface QuizGenerationService {

    List<Long> getQuiz(Long knodeId);

    List<Long> getQuiz(Long knodeId, String strategy);

    QuizStrategyData getQuizStrategy(Long knodeId);

    void setQuizStrategy(Long knodeId, String strategy);

}
