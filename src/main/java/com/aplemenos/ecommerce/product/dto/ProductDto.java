package com.aplemenos.ecommerce.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName,
        Instant createdAt
) {
}
