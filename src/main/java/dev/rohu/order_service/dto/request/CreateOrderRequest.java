package dev.rohu.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    @NotBlank(message = "customerName is mandatory")
    private String customerName;

    @NotNull(message = "amount is mandatory")
    @Positive(message = "amount must be greater than 0")
    private Double amount;

}
