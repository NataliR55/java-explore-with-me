package ru.practicum.util;

import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public interface ConvertDataTime {
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    static LocalDateTime formatDateTime(String dateTime) {
        LocalDateTime newDateTime;
        if (dateTime == null || dateTime.isBlank()) {
            throw new ValidationException("Date-time must be set");
        }
        try {
            newDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date-time format: " + dateTime);
        }
        return newDateTime;
    }

    static String dateTimeToString(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(dateTime);
    }
}
