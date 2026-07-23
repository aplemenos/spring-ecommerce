package com.aplemenos.ecommerce.payment.gateway;

/**
 * The result of asking a provider to start a payment: an opaque reference we
 * store, and the URL the customer is redirected to in order to pay.
 */
public record GatewaySession(
        String externalReference,
        String redirectUrl
) {
}
