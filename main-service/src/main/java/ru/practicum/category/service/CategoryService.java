package ru.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto updateCategoryDto, Long categoryId);

    Collection<CategoryDto> getAll(Pageable page);

    CategoryDto getCategoryById(Long id);

    void delete(Long id);
}
