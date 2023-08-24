package ru.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentAdminDto;
import ru.practicum.comment.dto.UpdateCommentUserDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto updateCommentByUser(UpdateCommentUserDto updateCommentUserDto, Long userId, Long commentId);

    CommentDto updateCommentByAdmin(Long commentId, UpdateCommentAdminDto commentDto);

    CommentDto getCommentsByIdByUser(Long userId, Long commentId);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getAllCommentByEvent(Long eventId, Pageable pageable);

    List<CommentDto> getAllByAdmin(Long eventId, PageRequest page);
}
