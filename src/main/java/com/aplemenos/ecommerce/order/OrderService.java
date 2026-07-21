package com.aplemenos.ecommerce.order;

import com.aplemenos.ecommerce.cart.Cart;
import com.aplemenos.ecommerce.cart.CartItem;
import com.aplemenos.ecommerce.cart.CartRepository;
import com.aplemenos.ecommerce.common.exception.EmptyCartException;
import com.aplemenos.ecommerce.common.exception.InsufficientStockException;
import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.order.dto.AdminOrderDto;
import com.aplemenos.ecommerce.order.dto.OrderDto;
import com.aplemenos.ecommerce.order.mapper.OrderMapper;
import com.aplemenos.ecommerce.product.Product;
import com.aplemenos.ecommerce.product.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Converts the user's cart into a PENDING order in a single transaction:
     * stock is decremented atomically per item, the order is built from price
     * snapshots, and the cart is emptied. Any failure (e.g. insufficient stock)
     * rolls the whole thing back — no partial orders, no lost stock.
     */
    @Transactional
    public OrderDto checkout(String email) {
        // A missing cart and an empty cart are equivalent to the user: nothing to buy.
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(EmptyCartException::new);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int qty = cartItem.getQuantity();

            // Race-safe: only succeeds if stock is still available at this instant
            int updated = productRepository.decrementStock(product.getId(), qty);
            if (updated == 0) {
                // Re-read the current stock purely for a helpful error message
                int available = productRepository.findById(product.getId())
                        .map(Product::getStock).orElse(0);
                throw new InsufficientStockException(product.getName(), qty, available);
            }

            OrderItem orderItem = new OrderItem(product, qty);
            order.addItem(orderItem);
            total = total.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        // Cart consumed: empty it so it can't be checked out twice
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(saved);
    }

    public List<OrderDto> findMyOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    // ----- Admin operations: no ownership restriction (access is gated by ADMIN role) -----

    public List<AdminOrderDto> findAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toAdminDto)
                .toList();
    }

    public AdminOrderDto findAnyOrder(Long orderId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        return orderMapper.toAdminDto(order);
    }

    // ----- Customer operations: scoped to the caller's own orders -----

    public OrderDto findMyOrder(String email, Long orderId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        // Ownership check: a user may only see their own orders. Sequential ids are
        // safe precisely because access is authorized here, not hidden by the id.
        if (!order.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Not your order");
        }

        return orderMapper.toDto(order);
    }
}
