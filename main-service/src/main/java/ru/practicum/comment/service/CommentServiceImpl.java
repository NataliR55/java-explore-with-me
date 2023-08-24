package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentAdminDto;
import ru.practicum.comment.dto.UpdateCommentUserDto;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        return null;
    }

    @Override
    public CommentDto updateCommentByUser(UpdateCommentUserDto updateCommentUserDto, Long userId, Long commentId) {
        return null;
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, UpdateCommentAdminDto commentDto) {
        return null;
    }

    @Override
    public CommentDto getCommentsByIdByUser(Long userId, Long commentId) {
        return null;
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {

    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {

    }

    @Override
    public List<CommentDto> getAllCommentByEvent(Long eventId, Pageable pageable) {
        return null;
    }

    @Override
    public List<CommentDto> getAllByAdmin(Long eventId, PageRequest page) {
        return null;
    }
}
