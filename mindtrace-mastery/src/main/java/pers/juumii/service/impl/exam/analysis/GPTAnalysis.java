package pers.juumii.service.impl.exam.analysis;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamAnalyzer;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.utils.DesignPatternUtils;

import java.util.List;

/**
 * 将测试数据喂给GPT让它生成一段文字评价
 */
@Service
public class GPTAnalysis implements ExamAnalyzer {

    private final CoreClient coreClient;

    @Autowired
    public GPTAnalysis(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    @Override
    public String analyze(ExamSession session) {
        ExamStrategyService strategyImpl = DesignPatternUtils.route(ExamStrategyService.class, strategy -> strategy.canHandle(session));
        List<QuizResult> quizResults = strategyImpl.extract(session);
        List<JSONObject> resp = quizResults.stream().map(
            result->JSONUtil.createObj()
                .set("chainStyleTitle", coreClient.chainStyleTitle(result.getKnodeId()))
                .set("completion", result.getCompletion())
                .set("duration", result.getDuration())).toList();
        return JSONUtil.parseArray(resp).toString();
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return true;
    }

    @Override
    public Boolean match(String analyzerName) {
        return analyzerName.equals(AnalyzerNames.GPT_ANALYSIS);
    }
}
