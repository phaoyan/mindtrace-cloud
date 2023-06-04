package pers.juumii.utils;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
    public static final String SPACED_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter SPACED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SPACED_DATE_TIME_PATTERN);
    public static final JSONConfig DEFAULT_DATE_TIME_PATTERN_CONFIG = JSONConfig.create().setDateFormat(DEFAULT_DATE_TIME_PATTERN);

    public static String format(LocalDateTime time) {
        if(time == null) return null;
        return time.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parse(String moment) {
        return LocalDateTime.parse(moment, DEFAULT_DATE_TIME_FORMATTER);
    }
}
