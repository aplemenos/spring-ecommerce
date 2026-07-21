package com.aplemenos.ecommerce.order.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) {
}
