package com.ecommerce.order.service;

import com.ecommerce.order.client.PaymentGatewayClient;
import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.exception.PaymentGatewayException;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orchestrates order placement:
 *   1. Fetch product + price from product-service        (Feign, interservice)
 *   2. Charge the customer via the external payment gateway (RestTemplate)
 *   3. On success, decrement stock in product-service     (Feign, interservice)
 *   4. Persist the order with its final status
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentGatewayClient paymentGatewayClient;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1. Look up the product (price, availability) from product-service
        ProductResponse product = productServiceClient.getProductById(request.getProductId());

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .totalAmount(totalAmount)
                .customerName(request.getCustomerName())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();
        order = orderRepository.save(order);

        // 2. Charge the customer through the external payment gateway
        ChargeRequest chargeRequest = new ChargeRequest(
                "ORDER-" + order.getId(),
                totalAmount,
                "USD",
                request.getCardNumber(),
                request.getCardExpiry(),
                request.getCardCvv(),
                request.getCustomerName()
        );

        try {
            ChargeResponse chargeResponse = paymentGatewayClient.charge(chargeRequest);
            order.setPaymentTransactionId(chargeResponse.getTransactionId());

            if (!"SUCCESS".equalsIgnoreCase(chargeResponse.getStatus())) {
                order.setStatus(OrderStatus.PAYMENT_FAILED);
                order.setFailureReason(chargeResponse.getGatewayMessage());
                orderRepository.save(order);
                return toResponse(order);
            }
        } catch (PaymentGatewayException ex) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setFailureReason(ex.getMessage());
            orderRepository.save(order);
            return toResponse(order);
        }

        // 3. Payment succeeded - reduce stock in product-service
        productServiceClient.reduceStock(product.getId(), request.getQuantity());

        // 4. Finalize
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        log.info("Order {} confirmed for customer {} (txn {})",
                order.getId(), order.getCustomerName(), order.getPaymentTransactionId());

        return toResponse(order);
    }

    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByCustomer(String customerName) {
        return orderRepository.findByCustomerNameIgnoreCase(customerName).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrThrow(id);
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .failureReason(order.getFailureReason())
                .orderDate(order.getOrderDate())
                .build();
    }
}
