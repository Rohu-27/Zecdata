package dev.rohu.order_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    private String orderId;
    private String customerName;
    private Double amount;
    private OrderStatus status;

}
