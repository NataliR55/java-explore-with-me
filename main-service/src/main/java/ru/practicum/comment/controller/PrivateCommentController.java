package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId) {
        return commentService.createCommentPrivate(newCommentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateCommentPrivate(updateCommentDto, commentId, userId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable(value = "userId") Long userId,
                                 @PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentPrivate(commentId, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentPrivate(commentId, userId);
    }
}
