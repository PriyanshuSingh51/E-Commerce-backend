package com.ecommerce.service;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.*;
import com.ecommerce.model.entity.*;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public OrderDTO createOrder(String userEmail, OrderCreateRequest request) {
        User user = userService.getUserByEmail(userEmail);
        log.info("Creating order for user: {}", userEmail);

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStock() + ", Requested: " + itemReq.getQuantity());
            }

            product.decreaseStock(itemReq.getQuantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                    .build();
            order.addOrderItem(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);
        log.info("Order created: {}", order.getOrderNumber());
        return toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(String userEmail, Pageable pageable) {
        User user = userService.getUserByEmail(userEmail);
        return orderRepository.findByUser(user, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        User user = userService.getUserByEmail(userEmail);
        if (!order.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new ResourceNotFoundException("Order", id);
        }
        return toDTO(order);
    }

    @Transactional
    public OrderDTO cancelOrder(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            item.getProduct().increaseStock(item.getQuantity());
            productRepository.save(item.getProduct());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(newStatus);
        return toDTO(orderRepository.save(order));
    }

    public List<Object[]> getDailyReport(LocalDateTime from) {
        return orderRepository.getDailyOrderReport(from);
    }

    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
               "-" + String.format("%03d", (int)(Math.random() * 1000));
    }

    OrderDTO toDTO(Order o) {
        OrderDTO dto = new OrderDTO();
        dto.setId(o.getId()); dto.setOrderNumber(o.getOrderNumber());
        dto.setTotalAmount(o.getTotalAmount()); dto.setStatus(o.getStatus().name());
        dto.setShippingAddress(o.getShippingAddress()); dto.setCreatedAt(o.getCreatedAt());
        if (o.getUser() != null) { dto.setUserId(o.getUser().getId()); dto.setUserEmail(o.getUser().getEmail()); }
        if (o.getOrderItems() != null) {
            dto.setItems(o.getOrderItems().stream().map(item -> {
                OrderItemDTO i = new OrderItemDTO();
                i.setId(item.getId()); i.setQuantity(item.getQuantity());
                i.setUnitPrice(item.getUnitPrice()); i.setSubtotal(item.getSubtotal());
                if (item.getProduct() != null) {
                    i.setProductId(item.getProduct().getId());
                    i.setProductName(item.getProduct().getName());
                }
                return i;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
