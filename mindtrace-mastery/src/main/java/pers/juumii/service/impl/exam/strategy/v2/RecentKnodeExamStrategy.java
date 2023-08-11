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
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecentKnodeExamStrategy implements ExamStrategyService {


    private final CoreClient coreClient;
    private final SamplingExamStrategyV2 samplingExamStrategyV2;

    @Autowired
    public RecentKnodeExamStrategy(
            CoreClient coreClient,
            SamplingExamStrategyV2 samplingExamStrategyV2) {
        this.coreClient = coreClient;
        this.samplingExamStrategyV2 = samplingExamStrategyV2;
    }

    /**
     * config:{
     *     before: string //时间字符串
     *     after: string  //时间字符串
     * }
     * cache:{
     *     selected: string[] // 采样选中的knode id
     *     corrects: string[] // 正确knode的id
     *     mistakes: string[] // 错误knode的id
     *     current: string // 当前knode的id
     * }
     * types: ["main", "statistics", "adjust"]
     */
    @Override
    public ExamInteract response(ExamSession session, ExamInteract req) {
        if(session.cache() == null)
            initCache(session);
        return switch (req.type()){
            case "main" -> main(session, req);
            case "statistics" -> statistics(session, req);
            case "adjust" -> adjust(session, req);
            default -> null;
        };
    }

    /**
     * 用于调整recent的时间区间
     * request:{
     *     type: "adjust"
     *     before: string //时间字符串
     *     after: string //时间字符串
     * }
     * response:{
     *     type: "adjust"
     * }
     */
    private ExamInteract adjust(ExamSession session, ExamInteract req) {
        JSONObject message = req.message();
        String before = message.getStr("before");
        String after = message.getStr("after");
        session.setConfig("before", before)
                .setConfig("after", after)
                .updateCache("selected", List.class,
                    (selected)->select(session));
        return ExamInteract.prototype(session.getId(), ExamInteract.SYSTEM, "{type:\"adjust\"}");
    }

    private ExamInteract statistics(ExamSession session, ExamInteract req) {
        return samplingExamStrategyV2.statistics(session);
    }

    private ExamInteract main(ExamSession session, ExamInteract req) {
        return samplingExamStrategyV2.main(session, req);
    }

    private void initCache(ExamSession session) {
        List<Long> selected = select(session);
        if(selected == null || selected.isEmpty()) return;
        session.setCache(JSONUtil.createObj()
                .set("selected", selected.stream().map(Object::toString).toList())
                .set("corrects", new ArrayList<>())
                .set("mistakes", new ArrayList<>())
                .set("current", selected.get(0).toString())
                .toString());
    }

    private List<Long> select(ExamSession session) {
        Long rootId = session.getExam().getRootId();
        JSONObject config = session.config();
        String before = config.getStr("before");
        String after = config.getStr("after");
        LocalDateTime beforeTime = TimeUtils.parse(before);
        LocalDateTime afterTime = TimeUtils.parse(after);
        return coreClient.leaves(rootId).stream()
                .filter(knode-> TimeUtils.ordered(afterTime, TimeUtils.parse(knode.getCreateTime()), beforeTime))
                .map(knode-> Convert.toLong(knode.getId()))
                .toList();
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return ExamStrategyUtils.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.RECENT_KNODE);
    }
}
