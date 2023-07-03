package pers.juumii.service.impl.exam.strategy.v2;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.service.impl.exam.strategy.ExamStrategyData;

import java.util.List;

@Service
public class HotspotExamStrategyV2 implements ExamStrategyService {

    private final CoreClient coreClient;
    private final HeuristicExamStrategyV2 heuristicExamStrategy;

    @Autowired
    public HotspotExamStrategyV2(CoreClient coreClient, HeuristicExamStrategyV2 heuristicExamStrategy) {
        this.coreClient = coreClient;
        this.heuristicExamStrategy = heuristicExamStrategy;
    }

    /**
     * config:{
     *     threshold: number //错误数达到threshold后layer下沉一层
     * }
     * cache:{
     *     corrects: string[],
     *     mistakes: string[],
     *     currentCorrects: string[],
     *     currentMistakes: string[],
     *     layerId: string,
     *     mistakeMap: {
     *         <knodeId>: number,
     *         ...
     *     }
     * }
     */
    @Override
    public ExamInteract response(ExamSession session, ExamInteract req) {
        if(session.cache() == null)
            initCache(session);
        return switch (req.type()){
            case "main" -> main(session, req);
            case "statistics" -> statistics(session);
            default -> null;
        };
    }

    private void initCache(ExamSession session) {
        heuristicExamStrategy.initCache(session);
        session.updateCache("mistakeMap", JSONUtil.createObj());
    }

    private ExamInteract statistics(ExamSession session) {
        return heuristicExamStrategy.statistics(session);
    }

    /**
     * request:
     * {
     *     type: "main",
     *     knodeId: string,
     *     quizIds: string[],
     *     completion: boolean
     * }
     * response:
     * {
     *     type: "main",
     *     layerId: string,
     *     knodeId: string,
     *     quizIds: string[]
     * }
     */
    private ExamInteract main(ExamSession session, ExamInteract req) {
        heuristicExamStrategy.updateVisited(session, req);
        updateMistakeMap(session, req);
        decideNext(session, req);
        heuristicExamStrategy.updateLayerId(session, req);
        heuristicExamStrategy.clearCurrentIfNecessary(session, req);
        return heuristicExamStrategy.mainResp(session);
    }

    private void decideNext(ExamSession session, ExamInteract req) {
        JSONObject cache = session.cache();
        String layerId = cache.getStr("layerId");
        JSONObject mistakeMap = cache.getJSONObject("mistakeMap");
        int curMisSize = mistakeMap.getInt(layerId);
        Integer threshold = session.config().getInt("threshold");
        req.updateMessage("next", String.class, (next)->curMisSize >= threshold ? "bottom" : "right");
    }

    private void updateMistakeMap(ExamSession session, ExamInteract req) {
        String layerId = session.cache().getStr("layerId");
        JSONObject mistakeMap = session.cache().getJSONObject("mistakeMap");
        if(mistakeMap == null){
            session.updateCache("mistakeMap", JSONUtil.createObj());
            mistakeMap = session.cache().getJSONObject("mistakeMap");
        }
        if(!mistakeMap.containsKey(layerId)) mistakeMap.clear();
        if(mistakeMap.isEmpty()){
            List<String> branchIds = coreClient.stem(Convert.toLong(layerId)).getBranchIds();
            JSONObject map = JSONUtil.createObj();
            for(String brId: branchIds) map.set(brId, 0);
            session.updateCache("mistakeMap", map);
        }
        JSONObject data = req.message();
        String knodeId = data.getStr("knodeId");
        if(knodeId == null) return;
        Boolean completion = data.getBool("completion");
        if(!completion)
            session.updateCache("mistakeMap", JSONObject.class, (map)->{
                Integer mistakes = map.getInt(layerId);
                return map.set(layerId, mistakes + 1);
            });
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return heuristicExamStrategy.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.HOTSPOT);
    }
}
