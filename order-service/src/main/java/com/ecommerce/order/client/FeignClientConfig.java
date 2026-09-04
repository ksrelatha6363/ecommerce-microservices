package com.ecommerce.order.client;

import com.ecommerce.order.exception.InsufficientStockException;
import com.ecommerce.order.exception.ProductNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ProductClientErrorDecoder();
    }

    static class ProductClientErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 404) {
                return new ProductNotFoundException("Product not found (via product-service)");
            }
            if (response.status() == 409) {
                return new InsufficientStockException("Insufficient stock (via product-service)");
            }
            return defaultDecoder.decode(methodKey, response);
        }
    }
}
