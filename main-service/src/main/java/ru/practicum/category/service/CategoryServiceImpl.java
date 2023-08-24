package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        if (categoryRepository.countByName(name, null) > 0) {
            throw new ConflictException(String.format("Category with name: %s already exist.", name));
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public CategoryDto update(NewCategoryDto newCategoryDto, Long categoryId) {
        String name = newCategoryDto.getName();
        if (categoryRepository.countByName(name, categoryId) > 0) {
            throw new ConflictException(String.format("Category with name: %s already exist.", name));
        }
        Category category = getCategoryById(categoryId);
        category.setName(name);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Category category = getCategoryById(id);
        if (eventRepository.existsByCategory(category)) {
            throw new ConflictException(String.format(
                    "Cannot delete a category with id: %s. There are events related to this category.", id));
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Collection<CategoryDto> getAllCategory(Integer from,Integer size) {
        PageRequest page = PageRequest.of(from, size);
        return CategoryMapper.toListCategoryDto(categoryRepository.findAll(page).toList());
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException(String.format("Category with id: %d is not exists!", categoryId)));
    }

    @Override
    public CategoryDto getCategoryDtoById(Long id) {
        return CategoryMapper.toCategoryDto(getCategoryById(id));
    }
}
