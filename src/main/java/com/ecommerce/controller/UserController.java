package com.ecommerce.controller;

import com.ecommerce.model.dto.*;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userDetails.getUsername())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateProfile(userDetails.getUsername(), request)));
    }
}
