package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.QuizGenerationService;

import java.util.List;

@RestController
public class QuizController {

    private final QuizGenerationService quizGenerationService;

    @Autowired
    public QuizController(QuizGenerationService quizGenerationService) {
        this.quizGenerationService = quizGenerationService;
    }

    @GetMapping("/knode/{knodeId}/quiz")
    public List<String> getQuiz(@PathVariable Long knodeId){
        return quizGenerationService.getQuiz(knodeId).stream().map(Object::toString).toList();
    }

}
