package pers.juumii.service;

import pers.juumii.data.temp.Exam;
import pers.juumii.data.persistent.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamAnalysis;

import java.util.List;

public interface SessionService {
    ExamInteract interact(Long sessionId, ExamInteract info);
    ExamSession getSession(Long sessionId);
    // 若userId为null，则默认使用loginId
    List<ExamSession> getCurrentSession(Long userId);
    // 若userId为null，则默认使用loginId
    ExamSession start(Exam exam);

    ExamAnalysis finish(Long sessionId);
    // 用户想要中断测试，则调用interrupt将缓存删除
    void interrupt(Long sessionId);

}
