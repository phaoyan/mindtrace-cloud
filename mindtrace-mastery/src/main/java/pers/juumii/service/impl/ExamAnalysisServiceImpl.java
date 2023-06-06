package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.ExamInteract;
import pers.juumii.data.persistent.ExamResult;
import pers.juumii.data.persistent.relation.ExamResultExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.ExamInteractMapper;
import pers.juumii.mapper.ExamResultMapper;
import pers.juumii.mapper.relation.ExamResultExamInteractMapper;
import pers.juumii.service.ExamAnalysisService;
import pers.juumii.service.ExamAnalyzer;
import pers.juumii.utils.AuthUtils;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.DesignPatternUtils;

import java.util.List;
import java.util.Map;

@Service
public class ExamAnalysisServiceImpl implements ExamAnalysisService {

    private final ExamResultMapper examResultMapper;
    private final ExamResultExamInteractMapper ereiMapper;
    private final ExamInteractMapper examInteractMapper;
    private final CoreClient coreClient;


    @Autowired
    public ExamAnalysisServiceImpl(
            ExamResultMapper examResultMapper,
            ExamResultExamInteractMapper ereiMapper,
            ExamInteractMapper examInteractMapper,
            CoreClient coreClient) {
        this.examResultMapper = examResultMapper;
        this.ereiMapper = ereiMapper;
        this.examInteractMapper = examInteractMapper;
        this.coreClient = coreClient;
    }

    /**
     * 分析结果没有强的格式要求，txt文本、json都行，前端负责实现解析和显示这些结果
     */
    @Override
    public String analyze(ExamSession session) {
        return DesignPatternUtils.route(
                ExamAnalyzer.class,
                examAnalyzer -> examAnalyzer.canHandle(session))
                .analyze(session);
    }

    @Override
    public String analyze(ExamSession session, String analyzerName) {
        ExamAnalyzer analyzer = DesignPatternUtils.route(
                ExamAnalyzer.class,
                examAnalyzer -> examAnalyzer.match(analyzerName));
        return analyzer == null ? analyze(session) : analyzer.analyze(session);
    }


    public void initExamResult(ExamResult examResult){
        // 初始化 examInteracts
        LambdaQueryWrapper<ExamResultExamInteract> ereiWrapper = new LambdaQueryWrapper<>();
        ereiWrapper.eq(ExamResultExamInteract::getExamResultId, examResult.getId());
        examResult.setInteracts(
                ereiMapper.selectList(ereiWrapper).stream()
                        .map(erei->examInteractMapper.selectById(erei.getExamInteractId()))
                        .toList());
    }

    public List<ExamResult> getExamResults(Long userId){
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getUserId, userId);
        List<ExamResult> examResults = examResultMapper.selectList(wrapper);
        for (ExamResult examResult: examResults)
            initExamResult(examResult);
        return examResults;
    }

    @Override
    public ExamResult getExamResult(Long resultId){
        ExamResult res = examResultMapper.selectById(resultId);
        if(res == null) return null;
        initExamResult(res);
        return res;
    }

    public List<ExamResult> getExamResultsOfKnode(Long knodeId){
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getRootId, knodeId);
        List<ExamResult> examResults = examResultMapper.selectList(wrapper);
        for(ExamResult examResult: examResults)
            initExamResult(examResult);
        return examResults;
    }

    @Override
    public List<ExamResult> getExamResultsOfKnodeOffsprings(Long knodeId) {
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        return DataUtils.join(
                offsprings.stream()
                .map(knode->getExamResultsOfKnode(Convert.toLong(knode.getId())))
                .toList());
    }

    @Override
    @Transactional
    public void removeExamResult(Long resultId) {
        examResultMapper.deleteById(resultId);
        LambdaQueryWrapper<ExamResultExamInteract> ereiWrapper = new LambdaQueryWrapper<>();
        ereiWrapper.eq(ExamResultExamInteract::getExamResultId, resultId);
        List<ExamResultExamInteract> relationships = ereiMapper.selectList(ereiWrapper);
        if(relationships.isEmpty()) return;

        LambdaUpdateWrapper<ExamInteract> interactWrapper = new LambdaUpdateWrapper<>();
        interactWrapper.in(ExamInteract::getId, relationships.stream().map(ExamResultExamInteract::getExamInteractId).toList());
        examInteractMapper.delete(interactWrapper);
        LambdaUpdateWrapper<ExamResultExamInteract> relationWrapper = new LambdaUpdateWrapper<>();
        relationWrapper.eq(ExamResultExamInteract::getExamResultId, resultId);
        ereiMapper.delete(relationWrapper);
    }

    @Override
    public List<ExamAnalysis> getExamAnalyses(Long userId, String analyzerName) {
        return getExamResults(userId).stream().map(
            result-> new ExamAnalysis(
                ExamResult.transfer(result),
                analyze(ExamResult.toSession(result), analyzerName))).toList();
    }

    @Override
    public ExamAnalysis getExamAnalysis(Long resultId, String analyzerName) {
        ExamResult examResult = getExamResult(resultId);
        return examResult == null ? null :
                new ExamAnalysis(
                ExamResult.transfer(examResult),
                analyze(ExamResult.toSession(examResult), analyzerName));
    }

    @Override
    public List<ExamAnalysis> getExamAnalysesOfKnode(Long knodeId, String analyzerName) {
        return getExamResultsOfKnode(knodeId).stream()
                .map(r-> new ExamAnalysis(
                        ExamResult.transfer(r),
                        analyze(ExamResult.toSession(r), analyzerName)))
                .toList();
    }

    @Override
    public List<ExamAnalysis> getExamAnalyses(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<ExamAnalysis> getExamAnalysisOfKnodeOffsprings(Long knodeId, String analyzerName) {
        List<KnodeDTO> offsprings = coreClient.offsprings(knodeId);
        return DataUtils.join(
                offsprings.stream()
                .map(knode->getExamAnalysesOfKnode(Convert.toLong(knode.getId()), analyzerName))
                .toList());
    }
}
