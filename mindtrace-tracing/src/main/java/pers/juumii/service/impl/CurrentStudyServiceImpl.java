package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.temp.CurrentStudy;
import pers.juumii.dto.tracing.CurrentStudyDTO;
import pers.juumii.service.CurrentStudyService;
import pers.juumii.service.StudyTraceService;
import pers.juumii.service.TraceEnhancerRelService;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CurrentStudyServiceImpl implements CurrentStudyService {

    private final StringRedisTemplate redis;
    private final StudyTraceService studyTraceService;
    private final TraceEnhancerRelService traceEnhancerRelService;

    @Autowired
    public CurrentStudyServiceImpl(
            StringRedisTemplate redis,
            StudyTraceService studyTraceService,
            TraceEnhancerRelService traceEnhancerRelService) {
        this.redis = redis;
        this.studyTraceService = studyTraceService;
        this.traceEnhancerRelService = traceEnhancerRelService;
    }

    private String key(){
        return  "mindtrace::tracing::current::" + StpUtil.getLoginIdAsLong();
    }

    @Override
    public CurrentStudy startCurrentStudy() {
        String key = key();
        if(redis.opsForValue().get(key) != null)
            redis.opsForValue().getAndDelete(key);
        StudyTrace trace = StudyTrace.prototype(null);
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
        if(currentStudy.getTrace().getEndTime() == null)
            currentStudy.getTrace().setEndTime(LocalDateTime.now());
        studyTraceService.insertStudyTrace(StudyTrace.transfer(currentStudy.getTrace()));
        for(Long knodeId: currentStudy.getKnodeIds())
            studyTraceService.postTraceCoverage(currentStudy.getTrace().getId(), knodeId);
        for(Long enhancerId: currentStudy.getEnhancerIds())
            traceEnhancerRelService.postEnhancerTraceRel(currentStudy.getTrace().getId(), enhancerId);
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
        if(currentStudy.getTrace().getEndTime() != null) return currentStudy;
        currentStudy.getTrace().getPauseList().add(LocalDateTime.now());
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy continueCurrentStudy() {
        CurrentStudy currentStudy = getCurrentStudy();
        if(currentStudy.getTrace().getEndTime() != null) return currentStudy;
        currentStudy.getTrace().getContinueList().add(LocalDateTime.now());
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy updateStartTime(String startTime) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().setStartTime(TimeUtils.parse(startTime));
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public CurrentStudy updateEndTime(String endTime) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getTrace().setEndTime(TimeUtils.parse(endTime));
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy;
    }

    @Override
    public List<Long> addKnodeId(Long knodeId) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getKnodeIds().add(knodeId);
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy.getKnodeIds();
    }

    @Override
    public void removeKnodeId(Long knodeId) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getKnodeIds().removeIf(_knodeId->_knodeId.equals(knodeId));
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
    }

    @Override
    public List<Long> addTraceEnhancerRel(Long enhancerId) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getEnhancerIds().add(enhancerId);
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
        return currentStudy.getEnhancerIds();
    }

    @Override
    public void removeTraceEnhancerRel(Long enhancerId) {
        CurrentStudy currentStudy = getCurrentStudy();
        currentStudy.getEnhancerIds().removeIf(id->id.equals(enhancerId));
        redis.opsForValue().set(key(),JSONUtil.toJsonStr(CurrentStudy.transfer(currentStudy)));
    }


}
