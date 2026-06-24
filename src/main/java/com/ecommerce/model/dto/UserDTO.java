package com.ecommerce.model.dto;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
