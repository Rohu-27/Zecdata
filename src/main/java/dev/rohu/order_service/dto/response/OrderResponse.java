package dev.rohu.order_service.dto.response;

import dev.rohu.order_service.model.Order;
import dev.rohu.order_service.model.OrderStatus;
import lombok.Getter;

@Getter
public class OrderResponse {

    private String orderId;
    private String customerName;
    private Double amount;
    private OrderStatus status;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.orderId = order.getOrderId();
        response.customerName = order.getCustomerName();
        response.amount = order.getAmount();
        response.status = order.getStatus();
        return response;
    }

}
