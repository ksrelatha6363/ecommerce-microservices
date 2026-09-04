package com.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "price is required")
    @PositiveOrZero(message = "price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "quantity is required")
    @PositiveOrZero(message = "quantity cannot be negative")
    private Integer quantity;

    private String category;
}
