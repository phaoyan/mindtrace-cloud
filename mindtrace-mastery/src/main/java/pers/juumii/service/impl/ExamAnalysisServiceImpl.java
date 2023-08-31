package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qcloud.cos.COSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.config.COSConfig;
import pers.juumii.data.persistent.ExamResult;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.feign.CoreClient;
import pers.juumii.mapper.ExamResultMapper;
import pers.juumii.service.ExamAnalysisService;
import pers.juumii.service.ExamAnalyzer;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.DesignPatternUtils;

import java.util.List;
import java.util.Map;

@Service
public class ExamAnalysisServiceImpl implements ExamAnalysisService {

    private final ExamResultMapper examResultMapper;
    private final CoreClient coreClient;
    private final COSClient cosClient;
    private final COSConfig cosConfig;


    @Autowired
    public ExamAnalysisServiceImpl(
            ExamResultMapper examResultMapper,
            CoreClient coreClient,
            COSClient cosClient,
            COSConfig cosConfig) {
        this.examResultMapper = examResultMapper;
        this.coreClient = coreClient;
        this.cosClient = cosClient;
        this.cosConfig = cosConfig;
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


    public List<ExamResult> getExamResults(Long userId){
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getUserId, userId);
        return examResultMapper.selectList(wrapper);
    }

    @Override
    public ExamResult getExamResult(Long resultId){
        return examResultMapper.selectById(resultId);
    }

    public List<ExamResult> getExamResultsOfKnode(Long knodeId){
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getRootId, knodeId);
        return examResultMapper.selectList(wrapper);
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
        String bucket = cosConfig.getExamResultBucketName();
        String key = "exam/result/cache/" + resultId;
        cosClient.deleteObject(bucket, key);
    }

    @Override
    public List<ExamAnalysis> getExamAnalyses(Long userId, String analyzerName) {
        return getExamResults(userId).stream().map(
            result-> new ExamAnalysis(
                ExamResult.transfer(result),
                analyze(result.toSession(), analyzerName))).toList();
    }

    @Override
    public ExamAnalysis getExamAnalysis(Long resultId, String analyzerName) {
        ExamResult examResult = getExamResult(resultId);
        return examResult == null ? null :
                new ExamAnalysis(
                ExamResult.transfer(examResult),
                analyze(examResult.toSession(), analyzerName));
    }

    @Override
    public List<ExamAnalysis> getExamAnalysesOfKnode(Long knodeId, String analyzerName) {
        return getExamResultsOfKnode(knodeId).stream()
                .map(r-> new ExamAnalysis(
                    ExamResult.transfer(r),
                    analyze(r.toSession(), analyzerName)))
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
