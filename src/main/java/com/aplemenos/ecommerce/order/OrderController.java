package com.aplemenos.ecommerce.order;

import com.aplemenos.ecommerce.order.dto.OrderDto;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Places an order from the authenticated user's current cart. */
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(Principal principal) {
        OrderDto order = orderService.checkout(principal.getName());
        return ResponseEntity
                .created(URI.create("/api/orders/" + order.id()))
                .body(order);
    }

    @GetMapping
    public List<OrderDto> myOrders(Principal principal) {
        return orderService.findMyOrders(principal.getName());
    }

    @GetMapping("/{id}")
    public OrderDto myOrder(@PathVariable Long id, Principal principal) {
        return orderService.findMyOrder(principal.getName(), id);
    }
}
