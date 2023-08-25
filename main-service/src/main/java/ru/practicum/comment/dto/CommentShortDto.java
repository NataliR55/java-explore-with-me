package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.util.ConvertDataTime;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentShortDto {
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConvertDataTime.DATE_TIME_PATTERN)
    private LocalDateTime created;
    private String author;
    private String event;
}
