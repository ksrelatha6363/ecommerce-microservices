package com.ecommerce.paymentgateway.controller;

import com.ecommerce.paymentgateway.dto.ChargeRequest;
import com.ecommerce.paymentgateway.dto.ChargeResponse;
import com.ecommerce.paymentgateway.model.PaymentTransaction;
import com.ecommerce.paymentgateway.service.PaymentGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mimics the public API surface of a real payment gateway
 * (e.g. POST /v1/charges on Stripe, /payment/checkout on Razorpay).
 */
@RestController
@RequestMapping("/gateway/v1/payments")
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@Valid @RequestBody ChargeRequest request) {
        ChargeResponse response = paymentGatewayService.charge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentTransaction> getTransaction(@PathVariable String transactionId) {
        PaymentTransaction transaction = paymentGatewayService.getTransaction(transactionId);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }
}
