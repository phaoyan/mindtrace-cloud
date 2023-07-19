package pers.juumii.data.temp;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import pers.juumii.data.persistent.ExamResult;
import pers.juumii.dto.mastery.ExamSessionDTO;
import pers.juumii.service.impl.exam.strategy.ExamStrategyData;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * ExamSession 是即时生成的会话，不需要持久化到mysql，可以缓存到redis
 */
@Data
public class ExamSession {

    private Long id;
    private Exam exam;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ExamInteract> interacts;
    private String cache;

    public ExamInteract back1(){
        return interacts.isEmpty() ? null: interacts.get(interacts.size()-1);
    }

    public JSONObject back1Data(){
        return JSONUtil.parseObj(back1().getMessage());
    }

    public ExamInteract back2() {
        return interacts.size() < 2 ? null : interacts.get(interacts.size() - 2);
    }

    public JSONObject back2Data(){
        return JSONUtil.parseObj(back2().getMessage());
    }

    public JSONObject config(){
        return JSONUtil.parseObj(ExamStrategyData.data(this).getConfig());
    }

    public ExamSession setConfig(String key, Object value){
        ExamStrategyData data = ExamStrategyData.data(this);
        if(data.getConfig() == null)
            data.setConfig(new HashMap<>());
        data.getConfig().put(key, value);
        getExam().setExamStrategy(JSONUtil.toJsonStr(data));
        return this;
    }


    public JSONObject cache(){return cache == null ? null : JSONUtil.parseObj(cache);}

    public <T> ExamSession updateCache(String key, Class<T> cl, Function<T, T> lambda){
        JSONObject cache = cache();
        T ori = cache.get(key, cl);
        T updated = lambda.apply(ori);
        cache.set(key, updated);
        setCache(cache.toString());
        return this;
    }

    public ExamSession updateCache(String key, Object data){
        JSONObject cache = cache();
        cache.set(key, data);
        setCache(cache.toString());
        return this;
    }

    public static ExamSessionDTO transfer(ExamSession session) {
        ExamSessionDTO res = new ExamSessionDTO();
        res.setId(session.getId().toString());
        res.setExam(Exam.transfer(session.getExam()));
        res.setInteracts(ExamInteract.transfer(session.getInteracts()));
        res.setStartTime(TimeUtils.format(session.getStartTime()));
        res.setEndTime(TimeUtils.format(session.getEndTime()));
        return res;
    }

    public static List<ExamSessionDTO> transfer(List<ExamSession> sessions) {
        return sessions.stream().map(ExamSession::transfer).toList();
    }

    public static ExamSession prototype(Exam exam) {
        ExamSession res = new ExamSession();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setExam(exam);
        res.setStartTime(LocalDateTime.now());
        res.setEndTime(null);
        res.setInteracts(new ArrayList<>());
        return res;
    }

    public static ExamResult settle(ExamSession session) {
        ExamResult res = new ExamResult();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setRootId(session.getExam().getRootId());
        res.setUserId(session.getExam().getUserId());
        res.setStartTime(session.getStartTime());
        res.setEndTime(LocalDateTime.now());
        res.setExamStrategy(session.getExam().getExamStrategy());
        return res;
    }


    public ExamInteract former(ExamInteract interact) {
        int index = interacts.indexOf(interact);
        if(interacts.isEmpty() || index == 0 || index == -1)
            return null;
        return interacts.get(index-1);
    }

}
