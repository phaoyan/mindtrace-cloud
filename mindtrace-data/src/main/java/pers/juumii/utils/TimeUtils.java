package pers.juumii.utils;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
    public static final String SPACED_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter SPACED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SPACED_DATE_TIME_PATTERN);
    public static final JSONConfig DEFAULT_DATE_TIME_PATTERN_CONFIG = JSONConfig.create().setDateFormat(DEFAULT_DATE_TIME_PATTERN);

    public static String format(LocalDateTime time) {
        if(time == null) return null;
        return time.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String format(LocalDate date){
        if(date == null) return null;
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    public static LocalDateTime parse(String moment) {
        if(moment == null) return null;
        return LocalDateTime.parse(moment, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static LocalDate parseToDate(String date){
        if(date == null) return null;
        return LocalDate.parse(date, DEFAULT_DATE_FORMATTER);
    }

    public static boolean ordered(LocalDateTime A, LocalDateTime B, LocalDateTime C) {
        return A.isBefore(B) && B.isBefore(C);
    }

    public static boolean inCurrentMonth(LocalDateTime startTime) {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonth().getValue();
        return startTime.getYear() == year && startTime.getMonth().getValue() == month;
    }
}
