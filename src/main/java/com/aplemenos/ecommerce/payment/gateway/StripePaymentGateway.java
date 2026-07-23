package com.aplemenos.ecommerce.payment.gateway;

import com.aplemenos.ecommerce.order.Order;
import com.aplemenos.ecommerce.payment.Payment;
import com.aplemenos.ecommerce.payment.PaymentProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Stripe strategy.
 *
 * <p>This is a <b>simulation</b>: it mints a Stripe-shaped session reference and a
 * redirect URL without calling Stripe, so the app runs with no API keys. To go
 * live, add the {@code com.stripe:stripe-java} dependency and replace the body of
 * {@link #createSession} with a real Checkout Session:
 *
 * <pre>{@code
 * Session session = Session.create(SessionCreateParams.builder()
 *     .setMode(SessionCreateParams.Mode.PAYMENT)
 *     .setSuccessUrl(...).setCancelUrl(...)
 *     .addAllLineItem(... from order.getItems() ...)
 *     .build());
 * return new GatewaySession(session.getId(), session.getUrl());
 * }</pre>
 *
 * and verify the webhook signature with {@code Webhook.constructEvent(payload,
 * sigHeader, endpointSecret)} in {@link #parseWebhook}.
 */
@Component
public class StripePaymentGateway implements PaymentGateway {

    private final ObjectMapper objectMapper;
    private final String checkoutBaseUrl;

    public StripePaymentGateway(ObjectMapper objectMapper,
                                @Value("${payment.checkout-base-url}") String checkoutBaseUrl) {
        this.objectMapper = objectMapper;
        this.checkoutBaseUrl = checkoutBaseUrl;
    }

    @Override
    public PaymentProvider provider() {
        return PaymentProvider.STRIPE;
    }

    @Override
    public GatewaySession createSession(Order order, Payment payment) {
        // Stripe checkout session ids look like "cs_test_...".
        String reference = "cs_test_" + UUID.randomUUID().toString().replace("-", "");
        String redirectUrl = checkoutBaseUrl + "/stripe/checkout/" + reference;
        return new GatewaySession(reference, redirectUrl);
    }

    /**
     * Parses a Stripe-style event:
     * {@code {"type":"checkout.session.completed","data":{"object":{"id":"cs_test_..","payment_status":"paid"}}}}
     */
    @Override
    public WebhookResult parseWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String type = root.path("type").asText();
            JsonNode object = root.path("data").path("object");
            String reference = object.path("id").asText();
            boolean success = "checkout.session.completed".equals(type)
                    && "paid".equals(object.path("payment_status").asText());
            return new WebhookResult(reference, success);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Malformed Stripe webhook payload", ex);
        }
    }
}
