package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than zero")
    private Integer quantity;

    @NotBlank(message = "customerName is required")
    private String customerName;

    // --- Card details are forwarded to the payment gateway only; never persisted ---
    @NotBlank(message = "cardNumber is required")
    private String cardNumber;

    @NotBlank(message = "cardExpiry is required")
    private String cardExpiry;

    @NotBlank(message = "cardCvv is required")
    private String cardCvv;
}
