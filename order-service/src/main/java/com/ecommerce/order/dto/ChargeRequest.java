package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Matches payment-gateway-service's ChargeRequest contract.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String customerName;
}
