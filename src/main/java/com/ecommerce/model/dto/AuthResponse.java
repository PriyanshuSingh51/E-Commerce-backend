package com.ecommerce.model.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default private String type = "Bearer";
    private Long userId;
    private String email;
    private String role;
}
