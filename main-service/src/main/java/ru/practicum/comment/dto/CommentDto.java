package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.util.ConvertDataTime;

import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    private Long id;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConvertDataTime.DATE_TIME_PATTERN)
    private LocalDateTime created;
    private UserShortDto author;
    private EventShortDto event;
    private CommentStatus status;
}
