package com.ecommerce.model.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
}
