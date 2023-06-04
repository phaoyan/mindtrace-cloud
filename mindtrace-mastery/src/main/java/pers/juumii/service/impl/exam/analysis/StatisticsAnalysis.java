package pers.juumii.service.impl.exam.analysis;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamAnalyzer;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.DesignPatternUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对测试数据进行统计，计算各个的节点的 叶子完成度 / 叶子数目
 */
@Service
public class StatisticsAnalysis implements ExamAnalyzer {

    private final CoreClient coreClient;

    @Autowired
    public StatisticsAnalysis(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    /**
     * 返回数据格式：
     * JSON数组，其中的数据对象为
     * {
     *     knode: Knode // 测试的knode本身
     *     completion: number //完成度总分
     *     leaves: number //本次测试测试到的该knode下所有leaves的数目
     * }
     */
    @Override
    public String analyze(ExamSession session) {
        ExamStrategyService strategyImpl = DesignPatternUtils.route(ExamStrategyService.class, strategy -> strategy.canHandle(session));
        List<QuizResult> quizResults = strategyImpl.extract(session);
        List<KnodeDTO> offsprings = coreClient.offsprings(session.getExam().getRootId());
        HashMap<Long, Double> idToCompletion = new HashMap<>();
        HashMap<Long, Integer> idToLeafCount = new HashMap<>();
        for(KnodeDTO knode: offsprings){
            Long knodeId = Convert.toLong(knode.getId());
            idToCompletion.put(knodeId, 0.0);
            idToLeafCount.put(knodeId, 0);
        }
        for(QuizResult quizResult: quizResults){
            List<KnodeDTO> ancestors =
                    coreClient.ancestors(quizResult.getKnodeId())
                    .stream().filter(anc->idToCompletion.containsKey(Convert.toLong(anc.getId())))
                    .toList();
            for(KnodeDTO knode: ancestors){
                Long knodeId = Convert.toLong(knode.getId());
                Double oriCompletion = idToCompletion.get(knodeId);
                Integer oriLeafCount = idToLeafCount.get(knodeId);
                idToCompletion.put(knodeId, oriCompletion + quizResult.getCompletion());
                idToLeafCount.put(knodeId, oriLeafCount + 1);
            }
        }
        return JSONUtil.parseArray(
            offsprings.stream()
                .filter(knode->!idToLeafCount.get(Convert.toLong(knode.getId())).equals(0))
                .map(knode->JSONUtil.createObj()
                    .set("knode", knode)
                    .set("completion", idToCompletion.get(Convert.toLong(knode.getId())))
                    .set("leaves", idToLeafCount.get(Convert.toLong(knode.getId()))))
            .toList()).toString();
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return true;
    }

    @Override
    public Boolean match(String analyzerName) {
        return analyzerName.equals(AnalyzerNames.STATISTICS_ANALYSIS);
    }
}
