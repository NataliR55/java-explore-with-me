package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NewCommentDto {
    @NotBlank
    @Size(min = 5, max = 2000)
    private String text;
}
