package ru.practicum.category.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
}
