package com.ecommerce.model.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private LocalDateTime createdAt;
}
