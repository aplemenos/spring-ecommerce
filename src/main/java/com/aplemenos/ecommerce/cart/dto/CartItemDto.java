package com.aplemenos.ecommerce.cart.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) {
}
