package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.ExamResult;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.dto.mastery.ExamResultDTO;
import pers.juumii.service.ExamAnalysisService;

import java.util.List;
import java.util.Map;

@RestController
public class ExamAnalysisController {

    private final ExamAnalysisService examAnalysisService;

    @Autowired
    public ExamAnalysisController(ExamAnalysisService examAnalysisService) {
        this.examAnalysisService = examAnalysisService;
    }

    @GetMapping("/exam/result/{resultId}")
    public ExamResultDTO getExamResult(@PathVariable Long resultId){
        return ExamResult.transfer(examAnalysisService.getExamResult(resultId));
    }

    @GetMapping("/knode/{knodeId}/exam/result")
    public List<ExamResultDTO> getExamResultsOfKnode(@PathVariable Long knodeId){
        return ExamResult.transfer(examAnalysisService.getExamResultsOfKnode(knodeId));
    }

    @GetMapping("/knode/{knodeId}/offsprings/exam/result")
    public List<ExamResultDTO> getExamResultsOfKnodeOffsprings(@PathVariable Long knodeId){
        return ExamResult.transfer(examAnalysisService.getExamResultsOfKnodeOffsprings(knodeId));
    }

    @GetMapping("/exam/analysis/{resultId}")
    public ExamAnalysis getExamAnalysis(
            @PathVariable Long resultId,
            @RequestParam("analyzerName") String analyzerName){
        return examAnalysisService.getExamAnalysis(resultId, analyzerName);
    }

    @GetMapping("/exam/analysis")
    public List<ExamAnalysis> getExamAnalyses(
            @RequestParam("userId") Long userId,
            @RequestParam("analyzerName") String analyzerName){
        return examAnalysisService.getExamAnalyses(userId, analyzerName);
    }

    @GetMapping("/knode/{knodeId}/exam/analysis")
    public List<ExamAnalysis> getExamAnalysesOfKnode(
            @PathVariable Long knodeId,
            @RequestParam(value = "analyzerName", required = false) String analyzerName){
        return examAnalysisService.getExamAnalysesOfKnode(knodeId, analyzerName);
    }

    @GetMapping("/knode/{knodeId}/offsprings/exam/analysis")
    public List<ExamAnalysis> getExamAnalysisOfKnodeOffsprings(
            @PathVariable Long knodeId,
            @RequestParam("analyzerName") String analyzerName){
        return examAnalysisService.getExamAnalysisOfKnodeOffsprings(knodeId, analyzerName);
    }

    @PostMapping("/exam/analysis")
    public List<ExamAnalysis> getExamAnalyses(@RequestBody Map<String, Object> params){
        return examAnalysisService.getExamAnalyses(params);
    }

    @DeleteMapping("/exam/result/{resultId}")
    public void removeExamResult(@PathVariable Long resultId){
        examAnalysisService.removeExamResult(resultId);
    }

}
