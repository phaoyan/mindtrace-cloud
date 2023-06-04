package pers.juumii.service.impl.exam.strategy;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.service.QuizGenerationService;
import pers.juumii.utils.DataUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Service
public class HeuristicExamStrategy implements ExamStrategyService {

    private final CoreClient coreClient;
    private final QuizGenerationService quizGenerationService;

    @Autowired
    public HeuristicExamStrategy(
            CoreClient coreClient,
            QuizGenerationService quizGenerationService) {
        this.coreClient = coreClient;
        this.quizGenerationService = quizGenerationService;
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
    public ExamInteract response(ExamSession session) {
        if(session.cache() == null)
            session.setCache(initialCache(session));
        return switch (session.back1().type()){
            case "main" -> main(session);
            case "statistics" -> statistics(session);
            default -> null;
        };
    }

    /**
     * request:
     * {
     *     type: "statistics"
     * }
     * response:
     * {
     *     corrects: string[] //回答正确的knode id
     *     mistakes: string[] //回答错误的knode id
     *     currentCorrects: string[] //当前层回答正确的knode id
     *     currentMistakes: string[] //当前层回答错误的knode id
     *     layerId: string // 当前层knode id
     * }
     */
    private ExamInteract statistics(ExamSession session) {
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
    private ExamInteract main(ExamSession session) {
        // 提取req数据
        JSONObject req = session.back1Data();
        String next = req.getStr("next");
        String knodeId = req.getStr("knodeId");
        Boolean completion = req.getBool("completion");

        // 更新cache
        if(next != null)
            session.updateCache(
                "layerId",String.class,
                (layerId)-> switch (next){
                    case "top" -> top(layerId);
                    case "bottom" -> bottom(layerId);
                    case "right" -> right(layerId);
                    default -> layerId;
                });
        if(knodeId != null){
            if(completion){
                session.updateCache(
                    "corrects", JSONArray.class,
                    (corrects)->{
                        corrects.add(knodeId);
                        return corrects;
                    }).updateCache(
                    "currentCorrects", JSONArray.class,
                    (currentCorrects)->{
                        currentCorrects.add(knodeId);
                        return currentCorrects;
                    });
            }else {
                session.updateCache(
                    "mistakes", JSONArray.class,
                    (mistakes)->{
                        mistakes.add(knodeId);
                        return mistakes;
                    }).updateCache(
                    "currentMistakes", JSONArray.class,
                    (currentMistakes)->{
                        currentMistakes.add(knodeId);
                        return currentMistakes;
                    });
            }
        }

        // 找knodeId与quizIds
        JSONObject cache = session.cache();
        Long layerId = Convert.toLong(cache.getStr("layerId"));
        List<Long> corrects = cache.getJSONArray("corrects").stream().map(Convert::toLong).toList();
        List<Long> mistakes = cache.getJSONArray("mistakes").stream().map(Convert::toLong).toList();
        Long selected = sample(layerId, DataUtils.join(corrects, mistakes));
        List<Long> selectedQuizIds = quizGenerationService.getQuiz(selected);


        return ExamInteract.prototype(
                session.getId(),
                ExamInteract.SYSTEM,
                JSONUtil.createObj()
                    .set("type","main")
                    .set("layerId", layerId)
                    .set("knodeId", selected)
                    .set("quizIds", selectedQuizIds)
                .toString());
    }

    private Long sample(Long layerId, List<Long> visited) {
        List<KnodeDTO> toPick = coreClient.leaves(layerId).stream()
                .filter(leaf -> !visited.contains(Convert.toLong(leaf.getId()))).toList();
        if(toPick.isEmpty()) return null;
        return Convert.toLong(DataUtils.randomPick(toPick).getId());
    }

    private String right(String layerId) {
        KnodeDTO stem = coreClient.stem(Convert.toLong(layerId));
        int index = stem.getBranchIds().indexOf(layerId);
        return stem.getBranchIds().get((index + 1) % stem.getBranchIds().size());
    }

    private String bottom(String layerId) {
        KnodeDTO knode = coreClient.check(Convert.toLong(layerId));
        List<String> branchIds = knode.getBranchIds();
        return branchIds.isEmpty() ? layerId : branchIds.get(0);
    }

    private String top(String layerId) {
        return coreClient.check(Convert.toLong(layerId)).getStemId();
    }

    private String initialCache(ExamSession session) {
        return JSONUtil.createObj()
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("currentCorrects", new ArrayList<>())
                .set("currentMistakes", new ArrayList<>())
                .set("layerId", session.getExam().getRootId().toString())
                .toString();
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        List<List<QuizResult>> res = session.getInteracts().stream()
            .filter(interact->
                interact.getRole().equals(ExamInteract.LEARNER) &&
                interact.type().equals("main"))
            .map(interact->{
                JSONObject data = JSONUtil.parseObj(interact.getMessage());
                return QuizResult.prototype(
                    Convert.toLong(data.getStr("knodeId")),
                    data.getJSONArray("quizIds").toList(String.class).stream().map(Convert::toLong).toList(),
                    Duration.between(session.former(interact).getMoment(), interact.getMoment()),
                    data.getBool("completion") ? 1.0 : 0.0);
            }).toList();
        return DataUtils.join(res);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.HEURISTIC);
    }
}
