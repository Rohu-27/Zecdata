package dev.rohu.order_service.exception;

import dev.rohu.order_service.model.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException{

    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }

}
