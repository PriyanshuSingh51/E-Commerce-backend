package com.ecommerce.controller;

import com.ecommerce.model.dto.*;
import com.ecommerce.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment processing")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment for an order")
    public ResponseEntity<ApiResponse<PaymentDTO>> process(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed", paymentService.processPayment(request)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment details for an order")
    public ResponseEntity<ApiResponse<PaymentDTO>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentByOrderId(orderId)));
    }
}
