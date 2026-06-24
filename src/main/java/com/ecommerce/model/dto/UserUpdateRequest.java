package com.ecommerce.model.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserUpdateRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String phone;
}
