package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceCoverage;
import pers.juumii.data.temp.CurrentStudy;
import pers.juumii.dto.CurrentStudyDTO;
import pers.juumii.mq.StudyTraceExchange;
import pers.juumii.service.CurrentStudyService;
import pers.juumii.service.StudyTraceService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CurrentStudyServiceImpl implements CurrentStudyService {

    private final StringRedisTemplate redis;
    private final StudyTraceService studyTraceService;
    private final RabbitTemplate rabbit;


    @Autowired
    public CurrentStudyServiceImpl(
            StringRedisTemplate redis,
            StudyTraceService studyTraceService,
            RabbitTemplate rabbit) {
        this.redis = redis;
        this.studyTraceService = studyTraceService;
        this.rabbit = rabbit;
    }

    private String key(){
        return  "mindtrace::tracing::current::" + StpUtil.getLoginIdAsLong();
    }

    @Override
    public CurrentStudy startCurrentStudy(Long userId) {
        String key = key();
        if(redis.opsForValue().get(key) != null)
            redis.opsForValue().getAndDelete(key);
        StudyTrace trace = StudyTrace.prototype(userId);
        CurrentStudy currentStudy = CurrentStudy.prototype(trace);
        redis.opsForValue().set(key, JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy getCurrentStudy() {
        String data = redis.opsForValue().get(key());
        if(data != null)
            return CurrentStudy.transfer(JSONUtil.toBean(data, CurrentStudyDTO.class));
        return null;
    }

    @Override
    public void removeCurrentStudy() {
        redis.opsForValue().getAndDelete(key());
    }

    @Override
    @Transactional
    public StudyTrace settleCurrentStudy() {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().setEndTime(LocalDateTime.now());
        studyTraceService.insertStudyTrace(StudyTrace.transfer(currentStudy.getTrace()));
        for(TraceCoverage coverage: currentStudy.getCoverages())
            studyTraceService.postTraceCoverage(coverage.getTraceId(), coverage.getKnodeId());
        rabbit.convertAndSend(
                StudyTraceExchange.STUDY_TRACE_EVENT_EXCHANGE,
                StudyTraceExchange.ROUTING_KEY_SETTLE,
                JSONUtil.toJsonStr(currentStudy.getTrace()));

        removeCurrentStudy();
        return currentStudy.getTrace();
    }

    @Override
    public CurrentStudy editCurrentStudyTitle(String title){
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().setTitle(title);
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy pauseCurrentStudy() {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().getPauseList().add(LocalDateTime.now());
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy continueCurrentStudy() {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().getContinueList().add(LocalDateTime.now());
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public List<TraceCoverage> addTraceCoverage(List<Long> knodeIds) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getCoverages().addAll(knodeIds.stream()
            .map(knodeId->TraceCoverage.prototype(
                currentStudy.getTrace().getId(),
                knodeId))
            .toList());
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy.getCoverages();
    }

    @Override
    public void removeTraceCoverage(Long knodeId) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getCoverages().removeIf(coverage->coverage.getKnodeId().equals(knodeId));
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
    }
}
