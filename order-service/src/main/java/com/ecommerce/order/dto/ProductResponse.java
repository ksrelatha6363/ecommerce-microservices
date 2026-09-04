package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Mirrors product-service's ProductResponse. Kept as a separate copy (rather
 * than a shared library module) so each microservice stays independently
 * deployable/versioned - a common, deliberate trade-off in microservice APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
}
