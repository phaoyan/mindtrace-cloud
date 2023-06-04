package pers.juumii.service.impl.exam.analysis;

import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.service.ExamAnalyzer;

@Service
public class HotspotAnalysis implements ExamAnalyzer {
    @Override
    public String analyze(ExamSession session) {
        return null;
    }

    @Override
    public Boolean canHandle(ExamSession session) {
        return true;
    }

    @Override
    public Boolean match(String analyzerName) {
        return analyzerName.equals(AnalyzerNames.HOTSPOT_ANALYSIS);
    }
}
