package com.ecommerce.model.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
