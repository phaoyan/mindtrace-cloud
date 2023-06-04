package pers.juumii.service;

import pers.juumii.data.persistent.ExamResult;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.dto.mastery.ExamResultDTO;

import java.util.List;
import java.util.Map;

/**
 * 在解析strategy实现对每一个叶子节点能够找到确定掌握程度方法的基础上，
 * 通过一系列的knode task在用户层的完成度，给出一些分析结论
 */
public interface ExamAnalysisService {

    String analyze(ExamSession session);

    String analyze(ExamSession session, String analyzerName);

    List<ExamAnalysis> getExamAnalyses(Long userId, String analysisStrategy);

    ExamAnalysis getExamAnalysis(Long resultId, String analyzerName);

    List<ExamAnalysis> getExamAnalysesOfKnode(Long knodeId, String analyzerName);

    List<ExamAnalysis> getExamAnalyses(Map<String, Object> params);

    List<ExamAnalysis> getExamAnalysisOfKnodeOffsprings(Long knodeId, String analyzerName);

    ExamResult getExamResult(Long resultId);

    List<ExamResult> getExamResultsOfKnode(Long knodeId);

    List<ExamResult> getExamResultsOfKnodeOffsprings(Long knodeId);

    void removeExamResult(Long resultId);
}
