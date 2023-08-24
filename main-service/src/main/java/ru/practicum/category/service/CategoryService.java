package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto updateCategoryDto, Long categoryId);

    Collection<CategoryDto> getAllCategory(Integer from,Integer size);

    Category getCategoryById(Long categoryId);

    CategoryDto getCategoryDtoById(Long id);

    void delete(Long id);
}
