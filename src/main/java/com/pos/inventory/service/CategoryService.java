package com.pos.inventory.service;

import com.pos.inventory.dto.product.CategoryRequest;
import com.pos.inventory.dto.product.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    void deleteCategory(Long id);
}
