package pers.juumii.service;

import java.util.Map;

public interface StudyTraceQueryService {
    Map<String, Long> getStudyTimeDistribution(Long knodeId);

}
