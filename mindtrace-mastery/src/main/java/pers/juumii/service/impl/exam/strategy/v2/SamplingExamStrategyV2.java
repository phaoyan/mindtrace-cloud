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
public class SamplingExamStrategyV2 implements ExamStrategyService {

    private final CoreClient coreClient;
    private final QuizGenerationService quizGenerationService;

    @Autowired
    public SamplingExamStrategyV2(
            CoreClient coreClient,
            QuizGenerationService quizGenerationService) {
        this.coreClient = coreClient;
        this.quizGenerationService = quizGenerationService;
    }

    /**
     * config:{
     *     size: number // 取样规模
     * }
     * cache:{
     *     selected: string[] // 采样选中的knode id
     *     corrects: string[] // 正确的knode的id
     *     mistakes: string[] // 错误的knode的id
     *     current: string // 当前knode的id，若为-1则表示已经完成了所有knode
     * }
     * types: ["main", "statistics"]
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
        return ExamInteract.prototype(
                session.getId(),
                ExamInteract.SYSTEM,
                session.cache() != null ?
                    session.cache().toString():
                    "{\"type\":\"statistics\"}");
    }

    /**
     * request:
     * {
     *     type: "main"
     *     knodeId: string
     *     completion: boolean
     * }
     * response:
     * {
     *     type: "main",
     *     knodeId: string,
     *     quizIds: string[]
     * }
     */
    public ExamInteract main(ExamSession session, ExamInteract req) {
        updateVisited(session, req);
        return mainResp(session);
    }

    private ExamInteract mainResp(ExamSession session) {
        JSONObject cache = session.cache();
        if(cache == null || cache.getStr("current") == null)
            return ExamInteract.prototype(
                    session.getId(),
                    ExamInteract.SYSTEM,
                    JSONUtil.createObj()
                    .set("type", "main")
                    .toString());
        Long knodeId = Convert.toLong(cache.getStr("current"));
        List<Long> quizIds = quizGenerationService.getQuiz(knodeId);
        return ExamInteract.prototype(
                session.getId(),
                ExamInteract.SYSTEM,
                JSONUtil.createObj()
                .set("type", "main")
                .set("knodeId", knodeId.toString())
                .set("quizIds", quizIds.stream().map(Object::toString).toList())
                .toString());
    }

    private void updateVisited(ExamSession session, ExamInteract req) {
        JSONObject message = req.message();
        Long knodeId = Convert.toLong(message.getStr("knodeId"));
        Boolean completion = message.getBool("completion");
        if(knodeId == null || completion == null) return;
        if(completion)
            session.updateCache(
            "corrects", JSONArray.class,
            (corrects)->{
                corrects.add(knodeId);
                return corrects;
            });
        else
            session.updateCache(
            "mistakes", JSONArray.class,
            (mistakes)->{
                mistakes.add(knodeId);
                return mistakes;
            });
        JSONObject cache = session.cache();
        List<String> selected = cache.getJSONArray("selected").toList(String.class);
        List<String> corrects = cache.getJSONArray("corrects").toList(String.class);
        List<String> mistakes = cache.getJSONArray("mistakes").toList(String.class);
        List<String> visited = DataUtils.joinList(corrects, mistakes);
        List<String> unvisited = DataUtils.getAllIf(selected, id -> !visited.contains(id));
        if(unvisited.isEmpty())
            session.updateCache("current", String.class, (id)->"-1");
        else
            session.updateCache("current", String.class, (id)->unvisited.get(0));
    }

    private void initCache(ExamSession session) {
        List<String> selected = select(session);
        if(selected.isEmpty())
            throw new RuntimeException("SamplingExamStrategy: selected knodes are empty.");
        session.setCache(JSONUtil.createObj()
                .set("selected", selected)
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("current", selected.get(0))
                .toString());
    }

    private List<String> select(ExamSession session) {
        Integer size = session.config().getInt("size");
        List<KnodeDTO> leaves = coreClient.leaves(session.getExam().getRootId());
        return DataUtils.randomPick(leaves.stream().map(KnodeDTO::getId).toList(), size);
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return ExamStrategyUtils.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.SAMPLING);
    }
}
