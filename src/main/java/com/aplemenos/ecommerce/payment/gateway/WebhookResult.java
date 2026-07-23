package com.aplemenos.ecommerce.payment.gateway;

/**
 * A provider webhook, normalized to the two things our domain cares about:
 * which payment it concerns (by external reference) and whether it succeeded.
 */
public record WebhookResult(
        String externalReference,
        boolean success
) {
}
