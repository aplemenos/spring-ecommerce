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
 * PayPal strategy.
 *
 * <p>A <b>simulation</b> mirroring {@link StripePaymentGateway}: same interface,
 * PayPal-shaped reference and webhook payload. To go live, add the PayPal Server
 * SDK, create an Order in {@link #createSession} and return its approval link, and
 * verify the webhook signature in {@link #parseWebhook}.
 *
 * <p>The point of having a second provider is to show the abstraction pays off:
 * {@code PaymentService} is unchanged, and the factory routes to whichever
 * provider the customer chose.
 */
@Component
public class PaypalPaymentGateway implements PaymentGateway {

    private final ObjectMapper objectMapper;
    private final String checkoutBaseUrl;

    public PaypalPaymentGateway(ObjectMapper objectMapper,
                                @Value("${payment.checkout-base-url}") String checkoutBaseUrl) {
        this.objectMapper = objectMapper;
        this.checkoutBaseUrl = checkoutBaseUrl;
    }

    @Override
    public PaymentProvider provider() {
        return PaymentProvider.PAYPAL;
    }

    @Override
    public GatewaySession createSession(Order order, Payment payment) {
        // PayPal order ids are upper-case alphanumeric tokens.
        String reference = "PAYPAL-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String redirectUrl = checkoutBaseUrl + "/paypal/checkout/" + reference;
        return new GatewaySession(reference, redirectUrl);
    }

    /**
     * Parses a PayPal-style event:
     * {@code {"event_type":"PAYMENT.CAPTURE.COMPLETED","resource":{"id":"PAYPAL-..","status":"COMPLETED"}}}
     */
    @Override
    public WebhookResult parseWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("event_type").asText();
            JsonNode resource = root.path("resource");
            String reference = resource.path("id").asText();
            boolean success = "PAYMENT.CAPTURE.COMPLETED".equals(eventType)
                    && "COMPLETED".equals(resource.path("status").asText());
            return new WebhookResult(reference, success);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Malformed PayPal webhook payload", ex);
        }
    }
}
