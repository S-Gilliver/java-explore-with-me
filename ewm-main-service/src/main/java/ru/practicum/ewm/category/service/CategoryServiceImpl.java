package ru.practicum.ewm.category.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@Data
public class CategoryServiceImpl implements CategoryService {

    public final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto newCategory) {
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(CategoryMapper.mapToNewCategory(newCategory)));
    }

    @Override
    public void removeCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        checkCategoryExists(catId);
        Category oldCat = categoryRepository.findById(catId).get();
        oldCat.setName(categoryDto.getName());
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(oldCat));
    }

    @Override
    public List<CategoryDto> getCategories(PageRequest page) {
        return CategoryMapper.mapToCategoriesDto(categoryRepository.findAll(page));
    }

    @Override
    public CategoryDto getCategoryDtoById(Long catId) {
        checkCategoryExists(catId);
        return CategoryMapper.mapToCategoryDto(categoryRepository.findById(catId).get());
    }

    @Override
    public Category getCategoryByIdForService(Long catId) {
        checkCategoryExists(catId);
        return categoryRepository.findById(catId).get();
    }

    private void checkCategoryExists(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with Id =" + catId + " does not exist");
        }
    }
}
