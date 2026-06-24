package com.ecommerce.model.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
@Data
public class OrderCreateRequest {
    @NotNull @Size(min = 1) private List<OrderItemRequest> items;
    @NotBlank private String shippingAddress;
    private String paymentMethod;
}
