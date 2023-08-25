package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdAndStatus(Long eventId, CommentStatus commentStatus, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    boolean existsByIdAndAuthorId(Long commentId, Long authorId);

    Optional<Comment> findAllByIdAndAuthorId(Long commentId, Long authorId);
}
