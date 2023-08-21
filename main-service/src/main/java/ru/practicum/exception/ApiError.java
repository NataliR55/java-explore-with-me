package ru.practicum.exception;

import lombok.Getter;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.util.ConvertDataTime;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class ApiError {
    private final String message; //Сообщение об ошибке
    private final String reason; //Общее описание причины ошибки
    private final String status; //Код статуса HTTP-ответа,example: FORBIDDEN
    private final String timestamp; //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")

    public ApiError(Exception e, String reason, String status) {
        log.error(e.getMessage(), e);
        this.message = e.getMessage();
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(ConvertDataTime.DATE_TIME_FORMATTER);
    }
}
