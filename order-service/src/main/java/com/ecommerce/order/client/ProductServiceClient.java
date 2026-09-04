package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Declarative HTTP client for product-service. The "name" is resolved
 * through Eureka by spring-cloud-starter-loadbalancer, so no host/port is
 * hardcoded - this is the "Feign client for interservice communication"
 * requirement.
 */
@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}/reduce-stock")
    ProductResponse reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
