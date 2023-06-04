package pers.juumii.service.impl.exam.strategy;

import cn.hutool.core.collection.CollectionUtil;
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
public class SamplingExamStrategy implements ExamStrategyService {

    private final CoreClient coreClient;
    private final QuizGenerationService quizGenerationService;

    @Autowired
    public SamplingExamStrategy(
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
     *     corrects: string[]
     *     mistakes: string[]
     *     index: number // 当前knode的索引
     * }
     * types: ["main", "statistics"]
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

    private String initialCache(ExamSession session) {
        return JSONUtil.createObj()
                .set("selected", select(session))
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("index", 0)
                .toString();
    }

    private List<String> select(ExamSession session) {
        Integer size = session.config().getInt("size");
        List<KnodeDTO> leaves = coreClient.leaves(session.getExam().getRootId());
        return DataUtils.randomPick(leaves.stream().map(KnodeDTO::getId).toList(), size);
    }

    /**
     * request
     * {
     *     type: "statistics"
     * }
     * response:{
     *     type: "statistics"
     *     corrects: string[]
     *     mistakes: string[]
     *     total: number
     * }
     *
     */
    private ExamInteract statistics(ExamSession session) {
        JSONObject cache = session.cache();
        return session.response(
                JSONUtil.createObj()
                .set("corrects", cache.getJSONArray("corrects"))
                .set("mistakes", cache.getJSONArray("mistakes"))
                .set("total",cache.getJSONArray("selected").size())
                .toString());
    }

    /**
     * request:
     * {
     *     type: "main"
     *     knodeId: string
     *     quizIds: string[]
     *     completion: boolean
     * }
     * response:
     * {
     *     type: "main"
     *     knodeId: string,
     *     quizIds: string[]
     * }
     *
     */
    public ExamInteract main(ExamSession session){
        // 更新cache
        if(session.back1Data().getStr("knodeId") != null){
            JSONObject req = session.back1Data();
            Boolean completion = req.getBool("completion");
            if(completion)
                session.updateCache(
                    "corrects", JSONArray.class,
                    (corrects)->{
                        corrects.add(req.getStr("knodeId"));
                        return corrects;
                    });
            else session.updateCache(
                    "mistakes", JSONArray.class,
                    (mistakes)->{
                        mistakes.add(req.getStr("knodeId"));
                        return mistakes;
                    });
            session.updateCache("index", Integer.class, index->index+1);
        }

        // 返回数据
        JSONObject cache = session.cache();
        if(cache.getInt("index").equals(cache.getJSONArray("selected").size()))
            return session.response(
                    JSONUtil.createObj()
                    .set("type", "main")
                    .toString());
        String knodeId = cache.getJSONArray("selected").toList(String.class).get(cache.getInt("index"));
        List<Long> quizIds = quizGenerationService.getQuiz(Convert.toLong(knodeId));
        return session.response(
                JSONUtil.createObj()
                .set("type", "main")
                .set("knodeId", knodeId)
                .set("quizIds", quizIds.stream().map(Object::toString).toList())
                .toString());
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        List<List<QuizResult>> res = session.getInteracts().stream()
            .filter(interact->
                interact.getRole().equals(ExamInteract.LEARNER) &&
                interact.type().equals("main") &&
                JSONUtil.parseObj(interact.getMessage()).containsKey("knodeId"))
            .map(interact->JSONUtil.parseObj(interact.getMessage()))
            .map(data->QuizResult.prototype(
                Convert.toLong(data.getStr("knodeId")),
                DataUtils.strToLong(data.getJSONArray("quizIds").toList(String.class)),
                Duration.ZERO,
                data.getBool("completion") ? 1.0 : 0.0))
            .toList();
        return DataUtils.join(res);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        ExamStrategyData data = JSONUtil.toBean(session.getExam().getExamStrategy(), ExamStrategyData.class);
        return data.getType().equals(ExamStrategyData.SAMPLING);
    }
}
