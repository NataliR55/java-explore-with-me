package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@RequestBody @Valid UpdateCommentDto commentDto,
                             @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateCommentAdmin(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentAdmin(commentId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComment(@PathVariable Long eventId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getAllAdmin(eventId, from, size);
    }
}
