package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Matches payment-gateway-service's ChargeResponse contract.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse {
    private String transactionId;
    private String orderId;
    private BigDecimal amount;
    private String status; // SUCCESS | FAILED | PENDING
    private String gatewayMessage;
    private LocalDateTime processedAt;
}
