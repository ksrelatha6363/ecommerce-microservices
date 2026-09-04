package com.ecommerce.order.client;

import com.ecommerce.order.dto.ChargeRequest;
import com.ecommerce.order.dto.ChargeResponse;
import com.ecommerce.order.exception.PaymentGatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the external payment gateway. Uses a plain RestTemplate against
 * a configured base URL rather than Feign+Eureka, since a real payment
 * provider lives outside your service mesh and isn't discoverable.
 */
@Slf4j
@Component
public class PaymentGatewayClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentGatewayClient(RestTemplate paymentGatewayRestTemplate,
                                 @Value("${payment.gateway.base-url}") String baseUrl) {
        this.restTemplate = paymentGatewayRestTemplate;
        this.baseUrl = baseUrl;
    }

    public ChargeResponse charge(ChargeRequest request) {
        try {
            String url = baseUrl + "/gateway/v1/payments/charge";
            log.info("Calling external payment gateway at {} for order {}", url, request.getOrderId());
            ChargeResponse response = restTemplate.postForObject(url, request, ChargeResponse.class);
            if (response == null) {
                throw new PaymentGatewayException("Payment gateway returned an empty response");
            }
            return response;
        } catch (RestClientException ex) {
            log.error("Payment gateway call failed for order {}: {}", request.getOrderId(), ex.getMessage());
            throw new PaymentGatewayException("Unable to reach payment gateway: " + ex.getMessage());
        }
    }
}
