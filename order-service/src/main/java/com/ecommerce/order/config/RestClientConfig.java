package com.ecommerce.order.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Plain (non-load-balanced) RestTemplate for calling the external payment
 * gateway, which - unlike product-service - is not registered with Eureka.
 * This mirrors how you'd call a real third-party provider such as Stripe
 * or Razorpay: a fixed base URL, not service discovery.
 * <p>
 * Timeouts are set via a SimpleClientHttpRequestFactory rather than
 * RestTemplateBuilder.connectTimeout(Duration)/.readTimeout(Duration),
 * since those Duration-based builder methods were removed in Spring Boot 3.4
 * in favor of ClientHttpRequestFactorySettings. This approach compiles
 * cleanly on both 3.3.x and 3.4.x.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate paymentGatewayRestTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(10000);   // 10 seconds

        return builder.requestFactory(() -> factory).build();
    }
}
