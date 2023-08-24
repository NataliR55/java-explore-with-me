package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.exception.NotFoundException;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    default Comment getCommentById(Long id) {
        return findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Comment with id: %d is not exists!", id)));
    }

    boolean existsByIdAndAuthorId(Long id, Long authorId);

}
