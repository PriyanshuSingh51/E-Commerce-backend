package com.ecommerce.controller;

import com.ecommerce.model.dto.*;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order management")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userDetails.getUsername(), pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getById(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, userDetails.getUsername())));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderDTO>> create(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", orderService.createOrder(userDetails.getUsername(), request)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancel(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", orderService.cancelOrder(id, userDetails.getUsername())));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateStatus(
            @PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", orderService.updateOrderStatus(id, status)));
    }

    @GetMapping("/report/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Object[]>>> dailyReport(
            @RequestParam(defaultValue = "30") int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(ApiResponse.success(orderService.getDailyReport(from)));
    }
}
