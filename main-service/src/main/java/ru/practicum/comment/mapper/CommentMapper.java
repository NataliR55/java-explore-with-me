package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    private CommentMapper() {
    }

    public static Comment toComment(NewCommentDto commentDto, User user, Event event) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(CommentStatus.PENDING)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .created(comment.getCreated())
                .status(comment.getStatus())
                .build();
    }

    public static List<CommentDto> toListCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .text(comment.getText())
                .author(comment.getAuthor().getName())
                .created(comment.getCreated())
                .event(comment.getEvent() == null ? "" : comment.getEvent().getTitle())
                .build();
    }

    public static List<CommentShortDto> toListCommentShortDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }
}
