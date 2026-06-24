package com.ecommerce.controller;

import com.ecommerce.model.dto.*;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products (paginated)")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByCategory(categoryId, pageable)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> search(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(keyword, pageable)));
    }

    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> byPriceRange(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByPriceRange(min, max, pageable)));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> lowStock(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowStockProducts(threshold)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> create(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created", productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> update(@PathVariable Long id, @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
