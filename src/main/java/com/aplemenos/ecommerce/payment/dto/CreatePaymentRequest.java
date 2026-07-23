package com.aplemenos.ecommerce.payment.dto;

import com.aplemenos.ecommerce.payment.PaymentProvider;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long orderId,
        @NotNull PaymentProvider provider
) {
}
