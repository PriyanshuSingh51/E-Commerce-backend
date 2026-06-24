package com.ecommerce.service;

import com.ecommerce.exception.PaymentFailedException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.PaymentDTO;
import com.ecommerce.model.dto.PaymentRequest;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.Payment;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.model.enums.PaymentMethod;
import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentDTO processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new PaymentFailedException("Order is not in PENDING state: " + order.getStatus());
        }
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new PaymentFailedException("Payment already processed for order: " + order.getOrderNumber());
        }

        // Simulate payment processing
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        boolean paymentSuccess = simulatePayment();

        PaymentStatus status = paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(status)
                .transactionId(transactionId)
                .build();

        if (paymentSuccess) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info("Payment successful for order: {}, txnId: {}", order.getOrderNumber(), transactionId);
        } else {
            log.warn("Payment failed for order: {}", order.getOrderNumber());
            throw new PaymentFailedException("Payment processing failed. Please try again.");
        }

        return toDTO(paymentRepository.save(payment));
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));
        return toDTO(payment);
    }

    private boolean simulatePayment() {
        return Math.random() > 0.05; // 95% success rate simulation
    }

    PaymentDTO toDTO(Payment p) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(p.getId()); dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod().name()); dto.setStatus(p.getStatus().name());
        dto.setTransactionId(p.getTransactionId()); dto.setCreatedAt(p.getCreatedAt());
        if (p.getOrder() != null) {
            dto.setOrderId(p.getOrder().getId());
            dto.setOrderNumber(p.getOrder().getOrderNumber());
        }
        return dto;
    }
}
