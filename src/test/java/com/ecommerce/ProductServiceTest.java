package com.ecommerce;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.ProductCreateRequest;
import com.ecommerce.model.dto.ProductDTO;
import com.ecommerce.model.entity.Category;
import com.ecommerce.model.entity.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private ProductService productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();
        product = Product.builder().id(1L).name("Test Product").price(new BigDecimal("99.99"))
                .stock(10).category(category).isActive(true).build();
    }

    @Test
    void getAllProducts_ShouldReturnPage() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByIsActiveTrue(any())).thenReturn(page);
        Page<ProductDTO> result = productService.getAllProducts(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
    }

    @Test
    void getProductById_WhenExists_ShouldReturn() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductDTO result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_ShouldSaveAndReturn() {
        ProductCreateRequest req = new ProductCreateRequest();
        req.setName("New Product"); req.setPrice(new BigDecimal("49.99"));
        req.setStock(5); req.setCategoryId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(product);
        ProductDTO result = productService.createProduct(req);
        assertNotNull(result);
        verify(productRepository).save(any());
    }

    @Test
    void deleteProduct_ShouldDeactivate() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        productService.deleteProduct(1L);
        verify(productRepository).save(argThat(p -> !((Product)p).getIsActive()));
    }
}
