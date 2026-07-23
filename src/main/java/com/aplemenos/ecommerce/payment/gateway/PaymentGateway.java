package com.aplemenos.ecommerce.payment.gateway;

import com.aplemenos.ecommerce.order.Order;
import com.aplemenos.ecommerce.payment.Payment;
import com.aplemenos.ecommerce.payment.PaymentProvider;

/**
 * Strategy for a payment provider. Each provider (Stripe, PayPal, ...) implements
 * this once; {@code PaymentService} depends only on this interface and never on a
 * concrete provider, so adding a provider means adding a class — not editing
 * existing code (Open/Closed Principle).
 */
public interface PaymentGateway {

    /** Which provider this strategy handles; used by the factory to route requests. */
    PaymentProvider provider();

    /**
     * Starts a payment with the provider and returns the reference to persist plus
     * the URL to redirect the customer to. With a real SDK this is where you would
     * create a Stripe Checkout Session / PayPal Order and return its hosted URL.
     */
    GatewaySession createSession(Order order, Payment payment);

    /**
     * Parses (and, in production, cryptographically verifies) a provider webhook
     * into a normalized result. Each provider sends a different payload shape, so
     * this parsing is inherently provider-specific.
     */
    WebhookResult parseWebhook(String payload);
}
