package com.aplemenos.ecommerce.cart;

import com.aplemenos.ecommerce.cart.dto.AddCartItemRequest;
import com.aplemenos.ecommerce.cart.dto.CartDto;
import com.aplemenos.ecommerce.cart.dto.UpdateCartItemRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Singular "/api/cart" on purpose: the cart is always the authenticated user's
 * own, so no identifier appears in the URL and there is nothing to tamper with.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDto getCart(Principal principal) {
        return cartService.getCart(principal.getName());
    }

    @PostMapping("/items")
    public CartDto addItem(@Valid @RequestBody AddCartItemRequest request,
                           Principal principal) {
        return cartService.addItem(principal.getName(), request.productId(), request.quantity());
    }

    @PutMapping("/items/{productId}")
    public CartDto updateItem(@PathVariable Long productId,
                              @Valid @RequestBody UpdateCartItemRequest request,
                              Principal principal) {
        return cartService.updateItemQuantity(principal.getName(), productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartDto removeItem(@PathVariable Long productId, Principal principal) {
        return cartService.removeItem(principal.getName(), productId);
    }

    /**
     * Clears the cart's contents. The cart resource itself survives — it is 1:1
     * with the user and is only removed when the user is deleted.
     */
    @DeleteMapping("/items")
    public CartDto clearItems(Principal principal) {
        return cartService.clear(principal.getName());
    }
}
