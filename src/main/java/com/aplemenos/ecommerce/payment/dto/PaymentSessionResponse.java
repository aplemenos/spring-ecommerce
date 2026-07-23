package com.aplemenos.ecommerce.payment.dto;

import com.aplemenos.ecommerce.payment.PaymentProvider;
import com.aplemenos.ecommerce.payment.PaymentStatus;

/** Returned when a payment is initiated: where to send the customer to pay. */
public record PaymentSessionResponse(
        Long paymentId,
        Long orderId,
        PaymentProvider provider,
        PaymentStatus status,
        String redirectUrl,
        String externalReference
) {
}
