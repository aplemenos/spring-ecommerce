package com.aplemenos.ecommerce.order.dto;

import com.aplemenos.ecommerce.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Order view for admins — adds the owning customer's identity. */
public record AdminOrderDto(
        Long id,
        Long userId,
        String userEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> items,
        Instant createdAt
) {
}
