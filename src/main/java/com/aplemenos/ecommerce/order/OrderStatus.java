package com.aplemenos.ecommerce.order;

public enum OrderStatus {
    /** Created, awaiting payment. */
    PENDING,
    /** Payment succeeded. */
    PAID,
    /** Canceled before payment; reserved stock has been returned. */
    CANCELLED
}
