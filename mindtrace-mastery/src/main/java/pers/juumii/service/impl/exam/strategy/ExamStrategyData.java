package pers.juumii.service.impl.exam.strategy;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import pers.juumii.data.temp.ExamSession;

import java.util.Map;

@Data
public class ExamStrategyData {

    public static final String SAMPLING = "sampling";
    public static final String FULL_CHECK = "full check";
    public static final String HOTSPOT = "hotspot";
    public static final String HEURISTIC = "heuristic";


    private String type;
    private Map<String, Object> config;


    public static Boolean canHandle(ExamSession session, String strategy){
        return JSONUtil.toBean(session.getExam().getExamStrategy(), ExamStrategyData.class).getType().equals(strategy);
    }

    public static ExamStrategyData data(ExamSession session) {
        return JSONUtil.toBean(session.getExam().getExamStrategy(), ExamStrategyData.class);
    }
}
