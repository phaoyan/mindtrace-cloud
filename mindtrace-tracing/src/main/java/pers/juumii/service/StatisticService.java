package pers.juumii.service;

import java.time.Duration;

public interface StatisticService {

    Duration learningTimeSpent(Long userId, Long knodeId);


}
