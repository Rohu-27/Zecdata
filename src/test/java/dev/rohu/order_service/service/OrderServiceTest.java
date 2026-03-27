package dev.rohu.order_service.service;

import dev.rohu.order_service.dto.request.CreateOrderRequest;
import dev.rohu.order_service.dto.response.OrderResponse;
import dev.rohu.order_service.exception.InvalidStatusTransitionException;
import dev.rohu.order_service.exception.OrderNotFoundException;
import dev.rohu.order_service.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void createOrder_shouldReturnOrderWithStatusNew() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Alice");
        request.setAmount(250.0);

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getOrderId()).isNotBlank();
        assertThat(response.getCustomerName()).isEqualTo("Alice");
        assertThat(response.getAmount()).isEqualTo(250.0);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void getOrderById_shouldReturnCorrectOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Bob");
        request.setAmount(100.0);
        OrderResponse created = orderService.createOrder(request);

        OrderResponse fetched = orderService.getOrderById(created.getOrderId());

        assertThat(fetched.getOrderId()).isEqualTo(created.getOrderId());
        assertThat(fetched.getCustomerName()).isEqualTo("Bob");
    }

    @Test
    void getOrderById_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> orderService.getOrderById("non-existent-id"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("non-existent-id");
    }

    @Test
    void updateStatus_shouldTransitionNewToProcessing() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Charlie");
        request.setAmount(75.0);
        OrderResponse created = orderService.createOrder(request);

        OrderResponse updated = orderService.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void updateStatus_shouldTransitionProcessingToCompleted() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Diana");
        request.setAmount(500.0);
        OrderResponse created = orderService.createOrder(request);

        orderService.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);
        OrderResponse completed = orderService.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED);

        assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void updateStatus_shouldThrowOnInvalidTransition() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Eve");
        request.setAmount(300.0);
        OrderResponse created = orderService.createOrder(request);

        // NEW → COMPLETED is invalid
        assertThatThrownBy(() -> orderService.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void updateStatus_shouldThrowWhenOrderCompleted() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Frank");
        request.setAmount(150.0);
        OrderResponse created = orderService.createOrder(request);
        orderService.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);
        orderService.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED);

        // No transitions allowed from COMPLETED
        assertThatThrownBy(() -> orderService.updateOrderStatus(created.getOrderId(), OrderStatus.NEW))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void getAllOrders_shouldReturnAllCreatedOrders() {
        CreateOrderRequest r1 = new CreateOrderRequest();
        r1.setCustomerName("Grace"); r1.setAmount(50.0);

        CreateOrderRequest r2 = new CreateOrderRequest();
        r2.setCustomerName("Hank"); r2.setAmount(200.0);

        orderService.createOrder(r1);
        orderService.createOrder(r2);

        List<OrderResponse> all = orderService.getAllOrders();

        assertThat(all).hasSize(2);
    }
}