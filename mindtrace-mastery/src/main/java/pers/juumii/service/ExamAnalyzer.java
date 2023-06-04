package pers.juumii.service;

import pers.juumii.data.temp.ExamSession;

public interface ExamAnalyzer {
    String analyze(ExamSession session);

    Boolean canHandle(ExamSession session);

    Boolean match(String analyzerName);
}
