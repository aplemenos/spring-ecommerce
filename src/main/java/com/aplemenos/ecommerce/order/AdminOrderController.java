package com.aplemenos.ecommerce.order;

import com.aplemenos.ecommerce.order.dto.AdminOrderDto;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only order access: every order across all customers. The class-level
 * {@code @PreAuthorize} locks the whole path to ADMIN, so there is no per-method
 * ownership check — access is authorized by role, not by owner.
 */
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<AdminOrderDto> allOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    public AdminOrderDto anyOrder(@PathVariable Long id) {
        return orderService.findAnyOrder(id);
    }
}
