package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.QuizGenerationService;
import pers.juumii.service.impl.quiz.strategy.QuizStrategyData;

import java.util.List;

@RestController
public class QuizController {

    private final QuizGenerationService quizGenerationService;

    @Autowired
    public QuizController(QuizGenerationService quizGenerationService) {
        this.quizGenerationService = quizGenerationService;
    }

    @PostMapping("/knode/{knodeId}/quiz/strategy")
    public void setQuizStrategy(
            @PathVariable Long knodeId,
            @RequestBody String strategy){
        quizGenerationService.setQuizStrategy(knodeId, strategy);
    }

    @GetMapping("/knode/{knodeId}/quiz/strategy")
    public QuizStrategyData getQuizStrategy(@PathVariable Long knodeId){
        return quizGenerationService.getQuizStrategy(knodeId);
    }

    @GetMapping("/knode/{knodeId}/quiz")
    public List<Long> getQuiz(@PathVariable Long knodeId){
        return quizGenerationService.getQuiz(knodeId);
    }



}
