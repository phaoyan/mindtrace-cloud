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
import java.util.Objects;

@Service
public class HotspotExamStrategy implements ExamStrategyService {

    public static final String SAMPLING_RANDOM = "random";
    public static final String SAMPLING_BALANCED = "balanced";

    private final QuizGenerationService quizGenerationService;
    private final CoreClient coreClient;

    @Autowired
    public HotspotExamStrategy(
            QuizGenerationService quizGenerationService,
            CoreClient coreClient) {
        this.quizGenerationService = quizGenerationService;
        this.coreClient = coreClient;
    }

    /**
     * strategy config：
     * {
     *     threshold: number // 在同一领域犯了几个错误后触发缩小范围的机制
     *     sampling: string // 采样策略，可选“random”在当前层随机采样或“balanced”在当前层轮询采样
     * }
     * request 格式：
     * {
     *     layerId: string // 当前所在层的stem的id
     *     knodeId: string // 测试knode的id
     *     quizIds: string[] // 测试quiz的id
     *     completion: boolean // 是否通过了这个knode的quiz
     * }
     * response 格式：
     * {
     *     layerId: string // 当前所在层的knodeId
     *     knodeId: string // 分析得到的下一个待测knode的id
     *     quizIds: string[] // quizStrategyService返回的quizIds
     *     currentLayerMistakes: number // 当前层的错误数
     *     totalMistakes: number // 总错误数
     *     done: Boolean // 如果layer触底则认为本次测试完成，done设为true，其他情况done不存在或未false
     *     visited: string[] // 已经访问过的所有knodeId
     * }
     */
    @Override
    public ExamInteract response(ExamSession session) {
        Long layerId;
        Long knodeId;
        List<Long> quizIds;
        int currentLayerMistakes;
        int totalMistakes;
        List<Long> visited;
        //开始仅有第一次request而无response的情况
        if(session.getInteracts().size() == 1){
            KnodeDTO root = coreClient.check(Convert.toLong(session.getExam().getRootId()));
            if(!root.getBranchIds().isEmpty())
                layerId = Convert.toLong(DataUtils.randomPick(root.getBranchIds()));
            else layerId = Convert.toLong(root.getId());
            currentLayerMistakes = 0;
            totalMistakes = 0;
            visited = new ArrayList<>();
        }else {
            JSONObject req = session.back1Data();
            JSONObject resp = session.back2Data();

            layerId = Convert.toLong(req.getStr("layerId"));
            currentLayerMistakes = resp.getInt("currentLayerMistakes");
            totalMistakes = resp.getInt("totalMistakes");
            visited = new ArrayList<>(
                    resp.getJSONArray("visited").toList(String.class)
                    .stream().map(Convert::toLong).toList());
            // 更新 mistakes
            if(!req.getBool("completion")){
                currentLayerMistakes ++;
                totalMistakes ++;
            }

            // 尝试对当前层的上一层做一次采样，如果采样不成功，
            // 证明当前层同层的所有knode的leaves都已经被遍历过了，
            // 则认为这次测试结束
            if(sample(Convert.toLong(coreClient.check(layerId).getStemId()), visited) == null)
                return ExamInteract.prototype(
                        session.getId(),
                        ExamInteract.SYSTEM,
                        JSONUtil.createObj().set("done", true).toString());
            do{
                // 决定layer是否下沉
                KnodeDTO layer = coreClient.check(layerId);
                if(currentLayerMistakes >= session.config().getInt("threshold") || layer.getBranchIds().size() <= 1){
                    currentLayerMistakes = 0;
                    // 触底认为测试完成发送done信息
                    if(layer.getBranchIds().isEmpty())
                        return ExamInteract.prototype(
                                session.getId(),
                                ExamInteract.SYSTEM,
                                JSONUtil.createObj().set("done", true).toString());
                    layerId = Convert.toLong(DataUtils.randomPick(layer.getBranchIds()));
                }else {
                    KnodeDTO stem = coreClient.check(Convert.toLong(layer.getStemId()));
                    List<Long> branchIds = new ArrayList<>(stem.getBranchIds().stream().map(Convert::toLong).toList());
                    String sampling = session.config().getStr("sampling");
                    if(sampling.equals(SAMPLING_RANDOM)){
                        layerId = DataUtils.randomPick(branchIds);
                    }else if(sampling.equals(SAMPLING_BALANCED)){
                        layerId = branchIds.get((branchIds.indexOf(layerId) + 1) % branchIds.size());
                    }
                }
                // 由于前面验证过同层并非所有leaves都被遍历过，所以最终一定能够实现sample非空从而跳出循环
            } while (sample(layerId, visited) == null);
        }
        knodeId = sample(layerId, visited);
        quizIds = quizGenerationService.getQuiz(knodeId);
        visited.add(knodeId);
        return ExamInteract.prototype(
            session.getId(),
            ExamInteract.SYSTEM,
            JSONUtil.createObj()
                .set("layerId", layerId.toString())
                .set("knodeId", knodeId == null ? null : knodeId.toString())
                .set("quizIds", quizIds.stream().map(Objects::toString).toList())
                .set("currentLayerMistakes", currentLayerMistakes)
                .set("totalMistakes", totalMistakes)
                .set("visited", visited)
                .toString()
        );
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        List<List<QuizResult>> lists = session.getInteracts().stream()
            .filter(interact -> interact.getRole().equals(ExamInteract.LEARNER))
            .map(interact->toQuizResults(interact, session))
            .toList();
        return DataUtils.join(lists);
    }

    public static List<QuizResult> toQuizResults(ExamInteract interact, ExamSession session) {
        JSONObject data = JSONUtil.parseObj(interact.getMessage());
        String idStr = data.getStr("knodeId");
        JSONArray quizIdJsonArray = data.getJSONArray("quizIds");
        List<String> quizIdStrings = quizIdJsonArray == null ? new ArrayList<>() : quizIdJsonArray.toList(String.class);
        Boolean completion = data.getBool("completion");
        Long knodeId = Convert.toLong(idStr);
        List<Long> quizIds = quizIdStrings.stream().map(Convert::toLong).toList();
        return quizIds.stream().map(quizId ->
                    QuizResult.prototype(
                    knodeId, quizId,
                    Duration.between(session.former(interact).getMoment(), interact.getMoment()),
                    completion ? 1.0 : 0.0))
                .toList();
    }

    private Long sample(Long layerId, List<Long> visited) {
        List<KnodeDTO> toPick = coreClient.leaves(layerId).stream()
                .filter(leaf -> !visited.contains(Convert.toLong(leaf.getId()))).toList();
        if(toPick.isEmpty()) return null;
        return Convert.toLong(DataUtils.randomPick(toPick).getId());
    }


    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.HOTSPOT);
    }
}
