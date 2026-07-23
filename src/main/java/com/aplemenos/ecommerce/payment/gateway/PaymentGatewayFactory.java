package com.aplemenos.ecommerce.payment.gateway;

import com.aplemenos.ecommerce.payment.PaymentProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Resolves the {@link PaymentGateway} for a given provider.
 *
 * <p>Spring injects <em>every</em> bean implementing {@link PaymentGateway}, which
 * this factory indexes by {@link PaymentGateway#provider()}. Adding a new provider
 * is therefore just adding a new {@code @Component} — no change here, and none in
 * {@code PaymentService}.
 */
@Component
public class PaymentGatewayFactory {

    private final Map<PaymentProvider, PaymentGateway> gatewaysByProvider;

    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        this.gatewaysByProvider = gateways.stream()
                .collect(Collectors.toMap(PaymentGateway::provider, Function.identity()));
    }

    public PaymentGateway resolve(PaymentProvider provider) {
        PaymentGateway gateway = gatewaysByProvider.get(provider);
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment provider: " + provider);
        }

        return gateway;
    }
}
