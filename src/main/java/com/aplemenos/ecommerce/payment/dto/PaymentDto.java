package com.aplemenos.ecommerce.payment.dto;

import com.aplemenos.ecommerce.payment.PaymentProvider;
import com.aplemenos.ecommerce.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDto(
        Long id,
        Long orderId,
        PaymentProvider provider,
        PaymentStatus status,
        BigDecimal amount,
        String externalReference,
        Instant createdAt,
        Instant updatedAt
) {
}
