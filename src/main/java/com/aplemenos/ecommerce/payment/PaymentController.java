package com.aplemenos.ecommerce.payment;

import com.aplemenos.ecommerce.payment.dto.CreatePaymentRequest;
import com.aplemenos.ecommerce.payment.dto.PaymentDto;
import com.aplemenos.ecommerce.payment.dto.PaymentSessionResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Starts a payment for one of the caller's orders. Returns where to pay. */
    @PostMapping
    public PaymentSessionResponse initiate(@Valid @RequestBody CreatePaymentRequest request,
                                           Principal principal) {
        return paymentService.initiate(principal.getName(), request.orderId(), request.provider());
    }

    @GetMapping("/{id}")
    public PaymentDto getPayment(@PathVariable Long id, Principal principal) {
        return paymentService.getMyPayment(principal.getName(), id);
    }

    /**
     * Provider callback. Public (the provider is not a logged-in user); in
     * production the provider's signature header is verified inside the gateway.
     * Always returns 200 so the provider does not needlessly retry.
     */
    @PostMapping("/webhook/{provider}")
    public ResponseEntity<Void> webhook(@PathVariable PaymentProvider provider,
                                        @RequestBody String payload) {
        paymentService.handleWebhook(provider, payload);
        return ResponseEntity.ok().build();
    }
}
