package com.pos.inventory.service.impl;

import com.pos.inventory.dto.product.CategoryRequest;
import com.pos.inventory.dto.product.CategoryResponse;
import com.pos.inventory.entity.Category;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.CategoryRepository;
import com.pos.inventory.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created category with id: {}, name: {}", saved.getId(), saved.getName());
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updated = categoryRepository.save(category);
        return CategoryResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Deactivated category with id: {}", id);
    }
}
