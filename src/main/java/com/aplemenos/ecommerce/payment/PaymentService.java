package com.aplemenos.ecommerce.payment;

import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.order.Order;
import com.aplemenos.ecommerce.order.OrderRepository;
import com.aplemenos.ecommerce.order.OrderStatus;
import com.aplemenos.ecommerce.payment.dto.PaymentDto;
import com.aplemenos.ecommerce.payment.dto.PaymentSessionResponse;
import com.aplemenos.ecommerce.payment.gateway.GatewaySession;
import com.aplemenos.ecommerce.payment.gateway.PaymentGateway;
import com.aplemenos.ecommerce.payment.gateway.PaymentGatewayFactory;
import com.aplemenos.ecommerce.payment.gateway.WebhookResult;
import com.aplemenos.ecommerce.payment.mapper.PaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          PaymentGatewayFactory gatewayFactory,
                          PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.gatewayFactory = gatewayFactory;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Starts a payment for the caller's own PENDING order using the chosen provider.
     * The concrete provider is resolved through the factory, so this method never
     * references Stripe or PayPal directly.
     */
    @Transactional
    public PaymentSessionResponse initiate(String email, Long orderId, PaymentProvider provider) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (!order.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Not your order");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order " + orderId + " is already paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order " + orderId + " is cancelled");
        }

        PaymentGateway gateway = gatewayFactory.resolve(provider);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(provider);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());

        GatewaySession session = gateway.createSession(order, payment);
        payment.setExternalReference(session.externalReference());
        Payment saved = paymentRepository.save(payment);

        return new PaymentSessionResponse(
                saved.getId(), order.getId(), provider, saved.getStatus(),
                session.redirectUrl(), saved.getExternalReference());
    }

    /**
     * Handles a provider webhook. Idempotent: a payment is only acted on while it
     * is still PENDING, so duplicate webhooks (which providers do send) are no-ops.
     * On success the order transitions PENDING -> PAID.
     */
    @Transactional
    public void handleWebhook(PaymentProvider provider, String payload) {
        WebhookResult result = gatewayFactory.resolve(provider).parseWebhook(payload);

        Payment payment = paymentRepository.findByExternalReference(result.externalReference())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment for reference " + result.externalReference()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.info("Ignoring duplicate webhook for payment {} (already {})",
                    payment.getId(), payment.getStatus());
            return;
        }

        if (result.success()) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.getOrder().setStatus(OrderStatus.PAID);
            log.info("Payment {} succeeded; order {} marked PAID",
                    payment.getId(), payment.getOrder().getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.info("Payment {} failed", payment.getId());
        }

        paymentRepository.save(payment);
    }

    public PaymentDto getMyPayment(String email, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Payment", paymentId));

        if (!payment.getOrder().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Not your payment");
        }

        return paymentMapper.toDto(payment);
    }
}
