package com.aplemenos.ecommerce.order.dto;

import com.aplemenos.ecommerce.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> items,
        Instant createdAt
) {
}
