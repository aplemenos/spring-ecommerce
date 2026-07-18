package com.aplemenos.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long id,
        List<CartItemDto> items,
        Integer totalQuantity,
        BigDecimal totalPrice
) {
}
