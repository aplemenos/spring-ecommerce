package com.aplemenos.ecommerce.cart.mapper;

import com.aplemenos.ecommerce.cart.Cart;
import com.aplemenos.ecommerce.cart.CartItem;
import com.aplemenos.ecommerce.cart.dto.CartDto;
import com.aplemenos.ecommerce.cart.dto.CartItemDto;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    CartItemDto toItemDto(CartItem item);

    /**
     * Totals are derived rather than stored, so they can never drift out of sync
     * with the items or with a product's current price.
     */
    default CartDto toDto(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream()
                .map(this::toItemDto)
                .toList();

        int totalQuantity = items.stream()
                .mapToInt(CartItemDto::quantity)
                .sum();

        BigDecimal totalPrice = items.stream()
                .map(CartItemDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto(cart.getId(), items, totalQuantity, totalPrice);
    }
}
