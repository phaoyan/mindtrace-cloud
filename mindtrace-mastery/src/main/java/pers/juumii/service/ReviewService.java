package pers.juumii.service;

import java.util.List;

public interface ReviewService {
    void addReviewSchedule(Long knodeId, Long next);

    void removeReviewSchedule(Long knodeId, String date);

    List<String> getReviewKnodeIds(Long rootId, String date);

    void ackReview(Long knodeId, Long next);

    void startReviewMonitor(Long rootId);

    void finishReviewMonitor(Long rootId);

    List<Long> getReviewMonitorList(Long userId);

    Boolean isKnodeMonitored(Long knodeId);
}
