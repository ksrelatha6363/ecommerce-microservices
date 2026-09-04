package com.ecommerce.paymentgateway.service;

import com.ecommerce.paymentgateway.dto.ChargeRequest;
import com.ecommerce.paymentgateway.dto.ChargeResponse;
import com.ecommerce.paymentgateway.model.PaymentStatus;
import com.ecommerce.paymentgateway.model.PaymentTransaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stand-in for a real payment gateway's charge processing. Transactions are
 * kept in memory (a real provider would be backed by its own persistent
 * store on their side, which is opaque to us as the API consumer).
 * <p>
 * Deterministic test rule so behaviour is repeatable without mocking:
 *  - card numbers ending in "0000"  -> FAILED  (simulates a declined card)
 *  - anything else                  -> SUCCESS
 */
@Service
public class PaymentGatewayService {

    private final Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();

    public ChargeResponse charge(ChargeRequest request) {
        String transactionId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        LocalDateTime now = LocalDateTime.now();

        boolean declined = request.getCardNumber() != null && request.getCardNumber().endsWith("0000");

        PaymentStatus status = declined ? PaymentStatus.FAILED : PaymentStatus.SUCCESS;
        String message = declined
                ? "Card declined by issuing bank"
                : "Payment processed successfully";

        PaymentTransaction transaction = new PaymentTransaction(
                transactionId,
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency(),
                status,
                message,
                now
        );
        transactions.put(transactionId, transaction);

        return new ChargeResponse(
                transactionId,
                request.getOrderId(),
                request.getAmount(),
                status,
                message,
                now
        );
    }

    public PaymentTransaction getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }
}
