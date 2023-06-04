package pers.juumii.service.impl.quiz.strategy;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import pers.juumii.data.persistent.QuizStrategy;

import java.util.Map;

@Data
public class QuizStrategyData {

    public static final String OFFLINE_RANDOM = "offline random";
    public static final String OFFLINE_TYPE_PRIORITY = "offline type priority";
    public static final String BINDING = "binding";

    private String type;
    private Map<String, Object> config;

    public JSONObject config(){
        return JSONUtil.parseObj(config);
    }

    public static Boolean canHandle(QuizStrategy strategy, String type){
        return  strategy != null &&
                JSONUtil.toBean(strategy.getQuizStrategy(), QuizStrategyData.class)
                    .getType().equals(type);
    }

    public static QuizStrategyData data(String quizStrategy) {
        return JSONUtil.toBean(quizStrategy, QuizStrategyData.class);
    }
}
