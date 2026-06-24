package com.ecommerce.service;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.ProductCreateRequest;
import com.ecommerce.model.dto.ProductDTO;
import com.ecommerce.model.entity.Category;
import com.ecommerce.model.entity.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable).map(this::toDTO);
    }

    public ProductDTO getProductById(Long id) {
        return toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id)));
    }

    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable).map(this::toDTO);
    }

    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable).map(this::toDTO);
    }

    public Page<ProductDTO> getProductsByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
        return productRepository.findByPriceRange(min, max, pageable).map(this::toDTO);
    }

    public List<ProductDTO> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        Product product = Product.builder()
                .name(request.getName()).description(request.getDescription())
                .price(request.getPrice()).stock(request.getStock())
                .category(category).imageUrl(request.getImageUrl())
                .build();
        return toDTO(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        product.setName(request.getName()); product.setDescription(request.getDescription());
        product.setPrice(request.getPrice()); product.setStock(request.getStock());
        product.setCategory(category); product.setImageUrl(request.getImageUrl());
        return toDTO(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId()); dto.setName(p.getName()); dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice()); dto.setStock(p.getStock()); dto.setImageUrl(p.getImageUrl());
        dto.setIsActive(p.getIsActive()); dto.setCreatedAt(p.getCreatedAt());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        return dto;
    }
}
