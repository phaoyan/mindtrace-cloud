package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import pers.juumii.data.LearningTrace;
import pers.juumii.data.Mindtrace;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.dto.TraceInfo;

import java.util.List;

public interface LearningTraceService {

    SaResult startLearning(Long userId, TraceInfo traceInfo);

    SaResult finishLearning(Long userId, TraceInfo traceInfo);

    LearningTrace pauseLearning(Long userId, TraceInfo traceInfo);

    LearningTrace continueLearning(Long userId, TraceInfo traceInfo);

    List<Mindtrace> settleLearning(Long userId, TraceInfo traceInfo);

    // 检查该用户当前有无正在进行的LearningTrace，如果有则返回它，如果没有则返回空
    LearningTrace checkNow(Long userId);

    List<LearningTrace> checkAll(Long userId);

    LearningTrace checkLatest(Long userId);

    List<LearningTrace> checkKnodeRelatedLearningTraces(Long userId, Long knodeId);

    List<Long> getRelatedKnodeIdsOfLearningTrace(Long learningTraceId);

    SaResult removeLearningTraceById(Long traceId);
}
