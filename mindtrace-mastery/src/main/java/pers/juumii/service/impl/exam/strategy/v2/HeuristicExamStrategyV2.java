package pers.juumii.service.impl.exam.strategy.v2;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.service.QuizGenerationService;
import pers.juumii.service.impl.exam.strategy.ExamStrategyData;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeuristicExamStrategyV2 implements ExamStrategyService {

    private final QuizGenerationService quizGenerationService;
    private final CoreClient coreClient;

    @Autowired
    public HeuristicExamStrategyV2(
            QuizGenerationService quizGenerationService,
            CoreClient coreClient) {
        this.quizGenerationService = quizGenerationService;
        this.coreClient = coreClient;
    }

    /**
     * strategy config：
     * {
     *
     * }
     * cache格式：
     * {
     *     corrects: string[] //回答正确的knode id
     *     mistakes: string[] //回答错误的knode id
     *     currentCorrects: string[] //当前层回答正确的knode id
     *     currentMistakes: string[] //当前层回答错误的knode id
     *     layerId: string // 当前层knode id
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


    /**
     * request:
     * {
     *     type: "statistics"
     * }
     * response: 所有cache数据
     */
    public ExamInteract statistics(ExamSession session) {
        return ExamInteract.prototype(session.getId(), ExamInteract.SYSTEM, session.cache().toString());
    }


    /**
     * request:
     * {
     *     type: "main"
     *     next: "top" | "bottom" | "right"
     *     knodeId: string
     *     quizIds: string[]
     *     completion: boolean
     * }
     * response:
     * {
     *      type: "main"
     *      layerId: string
     *      knodeId: string
     *      quizIds: string[]
     * }
     */
    private ExamInteract main(ExamSession session, ExamInteract req) {
        updateVisited(session, req);
        updateLayerId(session, req);
        clearCurrentIfNecessary(session, req);
        return mainResp(session);
    }

    public ExamInteract mainResp(ExamSession session) {
        String layerId = session.cache().getStr("layerId");
        String knodeId = sample(session);
        List<Long> quizIds = knodeId == null ? new ArrayList<>(): quizGenerationService.getQuiz(Convert.toLong(knodeId));
        return ExamInteract.prototype(
                session.getId(),
                ExamInteract.SYSTEM,
                JSONUtil.createObj()
                .set("type", "main")
                .set("layerId", layerId)
                .set("knodeId", knodeId)
                .set("quizIds", quizIds.stream().map(Object::toString).toList())
                .toString());
    }

    public void clearCurrentIfNecessary(ExamSession session, ExamInteract req) {
        String next = req.message().getStr("next");
        if(next != null && (!next.equals("right")))
            session .updateCache("currentMistakes", JSONArray.class, (curMistakes)->JSONUtil.createArray())
                    .updateCache("currentCorrects", JSONArray.class, (curCorrects)->JSONUtil.createArray());
    }

    public String sample(ExamSession session) {
        JSONObject cache = session.cache();
        Long layerId = Convert.toLong(session.cache().getStr("layerId"));
        List<Long> corrects = cache.getJSONArray("corrects").stream().map(Convert::toLong).toList();
        List<Long> mistakes = cache.getJSONArray("mistakes").stream().map(Convert::toLong).toList();
        List<Long> visited = DataUtils.joinList(corrects, mistakes);
        List<KnodeDTO> toPick = coreClient.leaves(layerId).stream().filter(leaf -> !visited.contains(Convert.toLong(leaf.getId()))).toList();
        if(toPick.isEmpty()) return null;
        return DataUtils.randomPick(toPick).getId();
    }

    public void updateVisited(ExamSession session, ExamInteract req) {
        JSONObject data = req.message();
        String knodeId = data.getStr("knodeId");
        if(knodeId == null) return;
        Boolean completion = data.getBool("completion");
        if(completion){
            session.updateCache(
                "corrects", JSONArray.class,
                (corrects)->{
                    corrects.add(knodeId);
                    return corrects;
                }).updateCache(
                "currentCorrects", JSONArray.class,
                (curCorrects)->{
                    curCorrects.add(knodeId);
                    return curCorrects;
                });
        }else{
            session.updateCache(
                "mistakes", JSONArray.class,
                (mistakes)->{
                    mistakes.add(knodeId);
                    return mistakes;
                }).updateCache(
                "currentMistakes", JSONArray.class,
                (curMistakes)->{
                    curMistakes.add(knodeId);
                    return curMistakes;
                });
        }
    }

    public void updateLayerId(ExamSession session, ExamInteract req) {
        JSONObject data = req.message();
        String next = data.getStr("next");
        if(next == null) return;
        session.updateCache("layerId",String.class,
            (layerId)->switch (next){
                case "top" -> Convert.toLong(layerId).equals(session.getExam().getRootId()) ? layerId: top(layerId);
                case "bottom" -> bottom(layerId);
                case "right" -> right(layerId);
                default -> layerId;
            });
    }

    public String right(String layerId) {
        KnodeDTO stem = coreClient.stem(Convert.toLong(layerId));
        int index = stem.getBranchIds().indexOf(layerId);
        return stem.getBranchIds().get((index + 1) % stem.getBranchIds().size());
    }

    public String bottom(String layerId) {
        KnodeDTO knode = coreClient.check(Convert.toLong(layerId));
        List<String> branchIds = knode.getBranchIds();
        return branchIds.isEmpty() ? layerId : branchIds.get(0);
    }

    public String top(String layerId) {

        return coreClient.check(Convert.toLong(layerId)).getStemId();
    }

    public void initCache(ExamSession session) {
        KnodeDTO root = coreClient.check(session.getExam().getRootId());
        if(root.getBranchIds().isEmpty())
            throw new RuntimeException("HeuristicExamStrategy: Branches Not Exist.");
        String layerId = root.getBranchIds().get(0);
        session.setCache(
            JSONUtil.createObj()
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("currentCorrects", new ArrayList<>())
                .set("currentMistakes", new ArrayList<>())
                .set("layerId", layerId)
                .toString());
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return ExamStrategyUtils.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.HEURISTIC);
    }
}
