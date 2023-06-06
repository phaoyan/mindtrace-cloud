package pers.juumii.service.impl.exam.strategy.v2;

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
     *     corrects: number[] // 正确的knode的index
     *     mistakes: number[] // 错误的knode的index
     *     index: number // 当前knode的索引
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
    private ExamInteract statistics(ExamSession session) {
        return ExamInteract.prototype(session.getId(), ExamInteract.SYSTEM, session.cache().toString());
    }

    /**
     * request:
     * {
     *     type: "main"
     *     index: number
     *     completion: boolean
     * }
     * response:
     * {
     *     type: "main",
     *     index: number,
     *     knodeId: string,
     *     quizIds: string[]
     * }
     */
    private ExamInteract main(ExamSession session, ExamInteract req) {
        updateVisited(session, req);
        return mainResp(session);
    }

    private ExamInteract mainResp(ExamSession session) {
        JSONObject cache = session.cache();
        Integer index = cache.getInt("index");
        List<Long> selected = cache.getJSONArray("selected").toList(String.class).stream().map(Convert::toLong).toList();
        Long knodeId = selected.get(index);
        List<Long> quizIds = quizGenerationService.getQuiz(knodeId);
        return ExamInteract.prototype(
                session.getId(),
                ExamInteract.SYSTEM,
                JSONUtil.createObj()
                .set("type", "main")
                .set("index", index)
                .set("knodeId", knodeId.toString())
                .set("quizIds", quizIds.stream().map(Object::toString))
                .toString());
    }

    private void updateVisited(ExamSession session, ExamInteract req) {
        JSONObject message = req.message();
        Integer index = message.getInt("index");
        Boolean completion = message.getBool("completion");
        if(index == null || completion == null) return;
        if(completion){
            session.updateCache(
            "corrects", JSONArray.class,
            (corrects)->{
                corrects.add(index);
                return corrects;
            });
        }else
            session.updateCache(
            "mistakes", JSONArray.class,
            (mistakes)->{
                mistakes.add(index);
                return mistakes;
            });
        session.updateCache("index", Integer.class, i->i+1);
    }

    private void initCache(ExamSession session) {
        session.setCache(JSONUtil.createObj()
                .set("selected", select(session))
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("index", 0)
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
