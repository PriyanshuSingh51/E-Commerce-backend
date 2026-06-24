package com.ecommerce.service;

import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.CategoryDTO;
import com.ecommerce.model.entity.Category;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Cacheable("categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return toDTO(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id)));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.getParentCategoryId()));
            category.setParentCategory(parent);
        }
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) throw new ResourceNotFoundException("Category", id);
        categoryRepository.deleteById(id);
    }

    private CategoryDTO toDTO(Category c) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(c.getId()); dto.setName(c.getName()); dto.setDescription(c.getDescription());
        if (c.getParentCategory() != null) {
            dto.setParentCategoryId(c.getParentCategory().getId());
            dto.setParentCategoryName(c.getParentCategory().getName());
        }
        return dto;
    }
}
