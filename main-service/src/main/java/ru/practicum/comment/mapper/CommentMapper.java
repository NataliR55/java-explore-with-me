package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        //TODO
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                //.authorName(comment.getAuthor().getName())
                //.created(comment.getCreated())
                //.status(comment.getStatus())
                .build();
    }
}
