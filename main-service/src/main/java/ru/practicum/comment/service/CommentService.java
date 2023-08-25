package ru.practicum.comment.service;

import ru.practicum.comment.dto.*;

import java.util.List;

public interface CommentService {

    CommentDto createCommentPrivate(NewCommentDto newCommentDto, Long authorId, Long eventId);

    CommentDto updateCommentPrivate(UpdateCommentDto updateCommentUserDto, Long commentId, Long authorId);

    CommentDto getCommentPrivate(Long commentId, Long authorId);

    void deleteCommentPrivate(Long commentId, Long authorId);

    CommentDto updateCommentAdmin(Long commentId, UpdateCommentDto commentDto);

    void deleteCommentAdmin(Long commentId);

    List<CommentDto> getAllAdmin(Long eventId, int from, int size);

    List<CommentShortDto> getAllCommentEventPublic(Long eventId, int from, int size);
}
