package pers.juumii.service.impl.exam.analysis;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.service.ExamAnalyzer;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.utils.DesignPatternUtils;

import java.util.List;

@Service
public class HotspotAnalysis implements ExamAnalyzer {

    /**
     * 返回数据格式: knodeId[] //JSON数组，为错误knode的id
     */
    @Override
    public String analyze(ExamSession session) {
        JSONArray res = JSONUtil.createArray();
        ExamStrategyService strategyImpl = DesignPatternUtils.route(ExamStrategyService.class, strategy -> strategy.canHandle(session));
        List<QuizResult> quizResults = strategyImpl.extract(session);
        List<QuizResult> mistakes = quizResults.stream().filter(result -> result.getCompletion() < 0.1).toList();
        for(QuizResult mistake: mistakes)
            res.add(mistake.getKnodeId().toString());
        return res.toString();
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return true;
    }

    @Override
    public Boolean match(String analyzerName) {
        return analyzerName.equals(AnalyzerNames.HOTSPOT_ANALYSIS);
    }
}
