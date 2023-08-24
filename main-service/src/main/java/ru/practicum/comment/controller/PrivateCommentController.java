package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentUserDto;
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
    public CommentDto create(@RequestBody @Valid NewCommentDto newCommentDto,
                             @PathVariable(value = "userId") Long userId,
                             @PathVariable(value = "eventId") Long eventId) {
                return commentService.createComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@RequestBody @Valid UpdateCommentUserDto updateCommentUserDto,
                             @PathVariable(value = "userId") Long userId,
                             @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateCommentByUser(updateCommentUserDto, userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentsByIdByUser(userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable(value = "userId") Long userId,
                           @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }
}
