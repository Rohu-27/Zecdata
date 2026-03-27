package dev.rohu.order_service.service;

import dev.rohu.order_service.dto.request.CreateOrderRequest;
import dev.rohu.order_service.dto.response.OrderResponse;
import dev.rohu.order_service.exception.InvalidStatusTransitionException;
import dev.rohu.order_service.exception.OrderNotFoundException;
import dev.rohu.order_service.model.Order;
import dev.rohu.order_service.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        Order order = Order.builder()
                .orderId(orderId)
                .customerName(request.getCustomerName())
                .amount(request.getAmount())
                .status(OrderStatus.NEW)
                .build();
        orderStore.put(orderId, order);
        return OrderResponse.from(order);
    }

    public OrderResponse getOrderById(String orderId) {
        return OrderResponse.from(findOrThrow(orderId));
    }

    public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrThrow(orderId);
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderStore.values().stream()
                .map(OrderResponse::from)
                .toList();
    }

    private Order findOrThrow(String orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }
        return order;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case NEW -> next == OrderStatus.PROCESSING;
            case PROCESSING -> next == OrderStatus.COMPLETED;
            case COMPLETED -> false;
        };
        if (!valid) {
            throw new InvalidStatusTransitionException(current, next);
        }
    }

}
