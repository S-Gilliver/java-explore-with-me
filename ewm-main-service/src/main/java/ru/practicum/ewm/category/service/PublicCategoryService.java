package ru.practicum.ewm.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getCategories(PageRequest pageRequest);

    CategoryDto getCategoryById(int catId);
}
