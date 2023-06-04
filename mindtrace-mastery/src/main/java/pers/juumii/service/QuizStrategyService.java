package pers.juumii.service;

import pers.juumii.data.persistent.QuizStrategy;

import java.util.List;

// 负责解析 strategy
public interface QuizStrategyService {

    // 对strategy进行解析，返回这一strategy对应的enhancerId
    List<Long> getQuiz(QuizStrategy strategy);

    Boolean canHandle(QuizStrategy strategy);

}
