package com.ecommerce;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.model.dto.OrderCreateRequest;
import com.ecommerce.model.dto.OrderDTO;
import com.ecommerce.model.dto.OrderItemRequest;
import com.ecommerce.model.entity.*;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserService userService;
    @InjectMocks private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").firstName("Test")
                .lastName("User").role(Role.CUSTOMER).isActive(true).build();
        product = Product.builder().id(1L).name("Test Product")
                .price(new BigDecimal("99.99")).stock(10).isActive(true).build();
    }

    @Test
    void createOrder_WhenStockAvailable_ShouldSucceed() {
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L); itemReq.setQuantity(2);
        OrderCreateRequest req = new OrderCreateRequest();
        req.setItems(List.of(itemReq)); req.setShippingAddress("123 Main St");

        when(userService.getUserByEmail("test@test.com")).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        Order savedOrder = Order.builder().id(1L).orderNumber("ORD-001")
                .user(user).totalAmount(new BigDecimal("199.98")).build();
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderDTO result = orderService.createOrder("test@test.com", req);
        assertNotNull(result);
        verify(productRepository, times(1)).save(any());
        verify(orderRepository).save(any());
    }

    @Test
    void createOrder_WhenInsufficientStock_ShouldThrow() {
        product.setStock(1);
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L); itemReq.setQuantity(5);
        OrderCreateRequest req = new OrderCreateRequest();
        req.setItems(List.of(itemReq)); req.setShippingAddress("123 Main St");

        when(userService.getUserByEmail("test@test.com")).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder("test@test.com", req));
    }
}
