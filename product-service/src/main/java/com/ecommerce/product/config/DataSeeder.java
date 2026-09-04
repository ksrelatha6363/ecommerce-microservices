package com.ecommerce.product.config;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds a couple of demo products on startup so the APIs are testable
 * immediately after launch without a manual POST first.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("Wireless Mouse")
                    .description("2.4GHz wireless mouse with USB receiver")
                    .price(new BigDecimal("19.99"))
                    .quantity(150)
                    .category("Electronics")
                    .build());

            productRepository.save(Product.builder()
                    .name("Mechanical Keyboard")
                    .description("RGB backlit mechanical keyboard, blue switches")
                    .price(new BigDecimal("59.99"))
                    .quantity(80)
                    .category("Electronics")
                    .build());

            productRepository.save(Product.builder()
                    .name("Office Chair")
                    .description("Ergonomic mesh-back office chair")
                    .price(new BigDecimal("129.50"))
                    .quantity(30)
                    .category("Furniture")
                    .build());
        }
    }
}
