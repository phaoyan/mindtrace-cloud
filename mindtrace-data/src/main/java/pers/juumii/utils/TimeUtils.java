package pers.juumii.utils;

import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
    public static final String SPACED_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter SPACED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SPACED_DATE_TIME_PATTERN);
}
