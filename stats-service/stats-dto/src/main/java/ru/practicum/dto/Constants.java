package ru.practicum.dto;

import java.time.format.DateTimeFormatter;

public interface Constants {
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
}
