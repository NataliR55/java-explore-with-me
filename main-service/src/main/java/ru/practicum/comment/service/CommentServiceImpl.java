package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStateAction;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto createCommentPrivate(NewCommentDto newCommentDto, Long authorId, Long eventId) {
        User author = getUserById(authorId);
        Event event = getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("You can only create comments for published events!");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, author, event);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentPrivate(UpdateCommentDto updateComment, Long commentId, Long authorId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, authorId)) {
            throw new ForbiddenException(String.format("The comment id:%d and author id:%d is not exists!",
                    commentId, authorId));
        }
        Comment comment = getCommentById(commentId);
        CommentStatus commentStatus = getCommentStatus(updateComment.getStateAction());
        if (commentStatus != null) {
            if (CommentStatus.CANCELED.equals(commentStatus)) {
                comment.setStatus(commentStatus);
            } else {
                throw new ValidationException(String.format("Invalid comment stat action: %s",
                        updateComment.getStateAction()));
            }
        }
        String newText = updateComment.getText();
        if (newText != null && !newText.isBlank()) {
            comment.setText(updateComment.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentPrivate(Long commentId, Long authorId) {
        existsCommentByIdAndAuthorId(commentId, authorId);
        return CommentMapper.toCommentDto(getCommentById(commentId));
    }

    @Override
    @Transactional
    public void deleteCommentPrivate(Long commentId, Long authorId) {
        commentRepository.delete(getCommentByIdAndAuthorId(commentId, authorId));
    }

    @Override
    @Transactional
    public CommentDto updateCommentAdmin(Long commentId, UpdateCommentDto updateComment) {
        Comment comment = getCommentById(commentId);
        CommentStatus commentStatus = getCommentStatus(updateComment.getStateAction());
        if (commentStatus != null) {
            comment.setStatus(commentStatus);
        }
        String newText = updateComment.getText();
        if (newText != null && !newText.isBlank()) {
            comment.setText(updateComment.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        commentRepository.delete(getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllAdmin(Long eventId, int from, int size) {
        getEventById(eventId);
        PageRequest page = getPageRequest(from, size).withSort(Sort.by("created").descending());
        return CommentMapper.toListCommentDto(commentRepository.findAllByEventId(eventId, page));
    }

    @Override
    public List<CommentShortDto> getAllCommentEventPublic(Long eventId, int from, int size) {
        getEventById(eventId);
        PageRequest page = getPageRequest(from, size).withSort(Sort.by("created").descending());
        return CommentMapper.toListCommentShortDto(commentRepository
                .findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, page));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d is not exists!", id)));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %s not exist!", id)));
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Comment with id: %d is not exists!", id)));
    }

    private void existsCommentByIdAndAuthorId(Long commentId, Long authorId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, authorId)) {
            throw new NotFoundException(String.format("The comment id:%d and author id:%s is not exists!",
                    commentId, authorId));
        }
    }

    private Comment getCommentByIdAndAuthorId(Long commentId, Long authorId) {
        return commentRepository.findAllByIdAndAuthorId(commentId, authorId).orElseThrow(()
                -> new NotFoundException(String.format("The comment id:%d and author id:%s is not exists!",
                commentId, authorId)));
    }

    private CommentStatus getCommentStatus(CommentStateAction stateAction) {
        if (stateAction == null) {
            return null;
        }
        if (CommentStateAction.PUBLISH_COMMENT.equals(stateAction)) {
            return CommentStatus.PUBLISHED;
        }
        if (CommentStateAction.REJECT_COMMENT.equals(stateAction)) {
            return CommentStatus.CANCELED;
        }
        throw new ValidationException(String.format("Invalid comment state action: %s", stateAction));
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}
