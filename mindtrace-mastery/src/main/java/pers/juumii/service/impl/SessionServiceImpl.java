package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import pers.juumii.data.persistent.ExamResult;
import pers.juumii.data.persistent.relation.ExamResultExamInteract;
import pers.juumii.data.temp.Exam;
import pers.juumii.data.persistent.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.mapper.ExamInteractMapper;
import pers.juumii.mapper.ExamResultMapper;
import pers.juumii.mapper.relation.ExamResultExamInteractMapper;
import pers.juumii.service.ExamAnalysisService;
import pers.juumii.service.ExamStrategyService;
import pers.juumii.service.SessionService;
import pers.juumii.service.impl.exam.analysis.AnalyzerNames;
import pers.juumii.utils.DesignPatternUtils;
import pers.juumii.utils.TimeUtils;

import java.time.Duration;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    public static final String SESSION_PREFIX = "mindtrace::mastery::exam::session::";
    public static final String USER_MAPPING_PREFIX = "mindtrace::mastery::exam::user::";


    private final StringRedisTemplate redis;
    private final ExamAnalysisService analysisService;
    private final ExamResultMapper examResultMapper;
    private final ExamInteractMapper examInteractMapper;
    private final ExamResultExamInteractMapper ereiMapper;

    @Autowired
    public SessionServiceImpl(
            StringRedisTemplate redis,
            ExamAnalysisService analysisService,
            ExamResultMapper examResultMapper,
            ExamInteractMapper examInteractMapper,
            ExamResultExamInteractMapper ereiMapper) {
        this.redis = redis;
        this.analysisService = analysisService;
        this.examResultMapper = examResultMapper;
        this.examInteractMapper = examInteractMapper;
        this.ereiMapper = ereiMapper;
    }

    private void updateSession(ExamSession session){
        String key = SESSION_PREFIX+session.getId();
        String sessionStr = JSONUtil.toJsonStr(session, TimeUtils.DEFAULT_DATE_TIME_PATTERN_CONFIG);
        Duration timeout = Duration.ofDays(1);
        redis.opsForValue().set(key, sessionStr, timeout);
    }

    @Override
    public ExamInteract interact(Long sessionId, ExamInteract req) {
        if(sessionId == null)
            sessionId = req.getSessionId();
        if(req.getId() == null)
            req.setId(IdUtil.getSnowflakeNextId());
        ExamSession session = getSession(sessionId);
        ExamInteract resp = DesignPatternUtils.route(
                ExamStrategyService.class,
                _strategy->_strategy.canHandle(session))
                .response(session, req);
        session.getInteracts().add(req);
        session.getInteracts().add(resp);
        updateSession(session);
        return resp;
    }

    @Override
    public ExamSession getSession(Long sessionId) {
        String json = redis.opsForValue().get(SESSION_PREFIX + sessionId);
        return JSONUtil.toBean(json, TimeUtils.DEFAULT_DATE_TIME_PATTERN_CONFIG, ExamSession.class);
    }

    @Override
    public List<ExamSession> getCurrentSession(Long userId) {
        if(userId == null)
            userId = StpUtil.getLoginIdAsLong();

        String userMappingKey = USER_MAPPING_PREFIX+userId;
        JSONArray sessionIds = JSONUtil.parseArray(redis.opsForValue().get(userMappingKey));
        return sessionIds.stream().map(id->getSession(Convert.toLong(id))).toList();
    }

    @Override
    public ExamSession start(Exam exam) {
        exam.setId(IdUtil.getSnowflakeNextId());
        if(exam.getUserId() == null)
            exam.setUserId(StpUtil.getLoginIdAsLong());
        String userMappingKey = USER_MAPPING_PREFIX+exam.getUserId();
        String mappingJson = redis.opsForValue().get(userMappingKey);
        JSONArray sessionIds = JSONUtil.parseArray(mappingJson);
        // 暂且先限制一个user同一时间内只能存在一个session，这样省去了让用户选择session的麻烦
        if(!sessionIds.isEmpty())
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Current session not finished.");

        ExamSession session = ExamSession.prototype(exam);
        // 将session本身存入redis
        updateSession(session);
        // 将user与session的关联信息存入redis
        sessionIds.add(session.getId());
        redis.opsForValue().set(userMappingKey, JSONUtil.toJsonStr(sessionIds));
        return session;
    }

    @Override
    public ExamAnalysis finish(Long sessionId) {

        ExamSession session = getSession(sessionId);
        ExamResult res = ExamSession.settle(session);
        // 清除redis数据
        redis.opsForValue().getAndDelete(SESSION_PREFIX + sessionId);
        redis.opsForValue().getAndDelete(USER_MAPPING_PREFIX + session.getExam().getUserId());
        // 结算mysql数据
        insertCompletely(res);
        return new ExamAnalysis(ExamResult.transfer(res), analysisService.analyze(session, AnalyzerNames.STATISTICS_ANALYSIS));
    }


    @Override
    public void interrupt(Long sessionId) {
        ExamSession session = getSession(sessionId);
        redis.opsForValue().getAndDelete(SESSION_PREFIX + sessionId);
        redis.opsForValue().getAndDelete(USER_MAPPING_PREFIX + session.getExam().getUserId());
    }

    @Transactional
    public void insertCompletely(ExamResult examResult) {
        examResultMapper.insert(examResult);
        for(ExamInteract interact: examResult.getInteracts()){
            if(interact.getMessage().length() > 1024)
                interact.setMessage("TO LONG");
            examInteractMapper.insert(interact);
            ereiMapper.insert(ExamResultExamInteract.prototype(examResult.getId(), interact.getId()));
        }
    }

}
