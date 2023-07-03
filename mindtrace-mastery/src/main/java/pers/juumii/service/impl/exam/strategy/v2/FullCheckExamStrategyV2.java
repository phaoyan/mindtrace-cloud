package pers.juumii.service.impl.exam.strategy.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.service.impl.exam.strategy.ExamStrategyData;

import java.util.List;

@Service
public class FullCheckExamStrategyV2 implements ExamStrategyService {

    private final CoreClient coreClient;
    private final SamplingExamStrategyV2 samplingExamStrategyV2;

    @Autowired
    public FullCheckExamStrategyV2(
            CoreClient coreClient,
            SamplingExamStrategyV2 samplingExamStrategyV2) {
        this.coreClient = coreClient;
        this.samplingExamStrategyV2 = samplingExamStrategyV2;
    }

    @Override
    public ExamInteract response(ExamSession session, ExamInteract req) {
        if(session.config().getInt("size") == null){
            List<KnodeDTO> leaves = coreClient.leaves(session.getExam().getRootId());
            session.setConfig("size", leaves.size());
        }
        return samplingExamStrategyV2.response(session, req);
    }

    @Override
    public List<QuizResult> extract(ExamSession session) {
        return samplingExamStrategyV2.extract(session);
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return ExamStrategyData.canHandle(session, ExamStrategyData.FULL_CHECK);
    }
}
