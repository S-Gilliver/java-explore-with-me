package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto newCategory);

    void removeCategoryById(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryDtoById(Long catId);

    Category getCategoryByIdForService(Long catId);
}
