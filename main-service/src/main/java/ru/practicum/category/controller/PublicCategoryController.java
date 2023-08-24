package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequestMapping(path = "/categories")
@RestController
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getAllCategory(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.getAllCategory(from, size);
    }

    @GetMapping("{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryDtoById(@PathVariable Long catId) {
        return categoryService.getCategoryDtoById(catId);
    }
}
