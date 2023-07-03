package pers.juumii.service;


import pers.juumii.data.temp.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;

import java.util.List;

public interface ExamStrategyService {
    ExamInteract response(ExamSession session, ExamInteract req);
    List<QuizResult> extract(ExamSession session);
    Boolean canHandle(ExamSession session);

}
