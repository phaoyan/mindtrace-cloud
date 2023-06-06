package pers.juumii.data.persistent;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.mastery.ExamInteractDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Data
public class ExamInteract {
    public static final String LEARNER = "learner";
    public static final String SYSTEM = "system";

    @TableId
    private Long id;
    private Long sessionId;
    private String role;
    private String message; //json格式字符串
    private LocalDateTime moment;
    @TableLogic
    private Boolean deleted;

    public String type(){
        return JSONUtil.parseObj(message).getStr("type");
    }
    public JSONObject message(){ return JSONUtil.parseObj(message); }
    public <T> ExamInteract updateMessage(String key, Class<T> cl, Function<T, T> lambda){
        JSONObject message = message();
        T ori = message.get(key, cl);
        T updated = lambda.apply(ori);
        message.set(key, updated);
        setMessage(message.toString());
        return this;
    }

    public static ExamInteractDTO transfer(ExamInteract interact) {
        ExamInteractDTO res = new ExamInteractDTO();
        if(res.getId() != null)
            res.setId(interact.getId().toString());
        if(res.getSessionId() != null)
            res.setSessionId(interact.getSessionId().toString());
        res.setRole(interact.getRole());
        res.setMessage(interact.getMessage());
        if(res.getMoment() != null)
            res.setMoment(TimeUtils.format(interact.getMoment()));
        return res;
    }

    public static List<ExamInteractDTO> transfer(List<ExamInteract> interacts) {
        return interacts.stream().map(ExamInteract::transfer).toList();
    }

    public static ExamInteract fromDTO(ExamInteractDTO dto) {
        ExamInteract res = new ExamInteract();
        res.setId(Convert.toLong(dto.getId()));
        res.setSessionId(Convert.toLong(dto.getSessionId()));
        res.setRole(dto.getRole());
        res.setMessage(dto.getMessage());
        if(dto.getMoment() != null)
            res.setMoment(TimeUtils.parse(dto.getMoment()));
        else res.setMoment(LocalDateTime.now());
        return res;
    }

    public static ExamInteract prototype(Long sessionId, String role, String message){
        ExamInteract res = new ExamInteract();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setSessionId(sessionId);
        res.setMoment(LocalDateTime.now());
        res.setRole(role);
        res.setMessage(message);
        return res;
    }
}
