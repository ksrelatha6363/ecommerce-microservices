package com.ecommerce.paymentgateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {

    @NotBlank(message = "orderId is required")
    private String orderId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    private String currency;

    @NotBlank(message = "cardNumber is required")
    private String cardNumber;

    @NotBlank(message = "cardExpiry is required")
    private String cardExpiry;

    @NotBlank(message = "cardCvv is required")
    private String cardCvv;

    @NotBlank(message = "customerName is required")
    private String customerName;
}
