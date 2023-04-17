package pers.juumii.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TraceInfo {

    public static final String START_LEARNING = "start learning";
    public static final String FINISH_LEARNING = "finish learning";
    public static final String PAUSE_LEARNING = "pause learning";
    public static final String CONTINUE_LEARNING = "continue learning";
    public static final String SETTLE_LEARNING = "settle learning";


    private String type;
    private Map<String, Object> data;
    private List<MindtraceDTO> dtos;
}
