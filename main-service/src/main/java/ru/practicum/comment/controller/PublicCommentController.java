package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> getAllCommentByEvent(@PathVariable Long eventId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getAllCommentEventPublic(eventId, from, size);
    }
}
