package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto postCategoryAdmin(NewCategoryDto newCategoryDto);

    void deleteCategoryAdmin(int catId);

    CategoryDto patchCategoryAdmin(CategoryDto categoryDto, int catId);
}
