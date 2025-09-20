package com.devops.productservice.service;

import com.devops.productservice.exception.DuplicateResourceException;
import com.devops.productservice.exception.ResourceNotFoundException;
import com.devops.productservice.model.dto.request.CategoryRequestDto;
import com.devops.productservice.model.dto.response.CategoryResponseDto;
import com.devops.productservice.model.entity.Category;
import com.devops.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return mapToResponseDto(category);
    }

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        validateUniqueName(requestDto.getName());

        Category category = mapToEntity(requestDto);
        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category existingCategory = findCategoryById(id);

        if (!existingCategory.getName().equalsIgnoreCase(requestDto.getName())) {
            validateUniqueName(requestDto.getName());
        }

        modelMapper.map(requestDto, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return mapToResponseDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private void validateUniqueName(String name) {
        if (categoryRepository.existsByNameIgnoreCaseAndActiveTrue(name)) {
            throw new DuplicateResourceException("Category", "name", name);
        }
    }

    private Category mapToEntity(CategoryRequestDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }
}