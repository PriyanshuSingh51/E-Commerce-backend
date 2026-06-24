package com.ecommerce.model.dto;
import lombok.Data;
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private String parentCategoryName;
}
