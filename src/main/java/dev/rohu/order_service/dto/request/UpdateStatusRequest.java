package dev.rohu.order_service.dto.request;

import dev.rohu.order_service.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {

    @NotNull(message = "status is mandatory")
    private OrderStatus status;

}
