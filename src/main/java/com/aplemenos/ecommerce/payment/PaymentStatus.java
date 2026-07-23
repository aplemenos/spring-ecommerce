package com.aplemenos.ecommerce.payment;

public enum PaymentStatus {
    /** Session created, awaiting the provider's callback. */
    PENDING,
    /** Provider confirmed the payment. */
    SUCCEEDED,
    /** Provider reported the payment failed. */
    FAILED
}
