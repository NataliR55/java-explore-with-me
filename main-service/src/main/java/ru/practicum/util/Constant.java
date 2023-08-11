package ru.practicum.util;

import java.time.format.DateTimeFormatter;

public interface Constant {
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    String APP = "ewm-service";
}
