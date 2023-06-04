package pers.juumii.service.impl.exam.strategy;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamStrategyService;

import java.util.ArrayList;
import java.util.List;


@Service
public class FullCheckExamStrategy implements ExamStrategyService {


    private final CoreClient coreClient;
    private final SamplingExamStrategy samplingExamStrategy;

    @Autowired
    public FullCheckExamStrategy(
            CoreClient coreClient,
            SamplingExamStrategy samplingExamStrategy) {
        this.coreClient = coreClient;
        this.samplingExamStrategy = samplingExamStrategy;
    }

    /**
     * FullCheck是Sampling在取值到最大时的特例，所以直接调用sampling的实现方式
     */
    @Override
    public ExamInteract response(ExamSession session) {
        if(session.cache() == null)
            session.setCache(initialCache(session));
        return samplingExamStrategy.response(session);
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
        List<KnodeDTO> leaves = coreClient.leaves(session.getExam().getRootId());
        return leaves.stream().map(KnodeDTO::getId).toList();
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return samplingExamStrategy.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.FULL_CHECK);
    }
}
