package com.ecommerce.paymentgateway.dto;

import com.ecommerce.paymentgateway.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse {
    private String transactionId;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String gatewayMessage;
    private LocalDateTime processedAt;
}
