package ru.practicum.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.util.Constant;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public class ApiError {
    private List<String> errors; //Список стектрейсов или описания ошибок
    private final String message; //Сообщение об ошибке
    private final String reason; //Общее описание причины ошибки
    private final String status; //Код статуса HTTP-ответа,example: FORBIDDEN
    private final String timestamp; //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")

    public ApiError(Exception e, String reason, String status) {
        log.info(e.getMessage());
        this.errors = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
        this.message = e.getMessage();
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(Constant.DATE_TIME_FORMATTER);
    }
}
