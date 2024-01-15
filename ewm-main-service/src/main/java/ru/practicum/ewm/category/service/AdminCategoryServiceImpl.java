package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@RequiredArgsConstructor
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryDto postCategoryAdmin(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Category with this name already exists");
        }
        Category category = CategoryMapper.createCategory(newCategoryDto);
        return CategoryMapper.createCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCategoryAdmin(int categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryDto patchCategoryAdmin(CategoryDto categoryDto, int categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", categoryId)));

        if (categoryDto.getName().equals(category.getName())) {
            return CategoryMapper.createCategoryDto(category);
        }

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Category with this name already exists");
        }

        category.setName(categoryDto.getName());

        return CategoryMapper.createCategoryDto(categoryRepository.save(category));
    }
}

