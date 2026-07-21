package com.aplemenos.ecommerce.order.mapper;

import com.aplemenos.ecommerce.order.Order;
import com.aplemenos.ecommerce.order.OrderItem;
import com.aplemenos.ecommerce.order.dto.AdminOrderDto;
import com.aplemenos.ecommerce.order.dto.OrderDto;
import com.aplemenos.ecommerce.order.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    // product is a soft link (nulled if the product was deleted); productName and
    // unitPrice are snapshots on the order item itself, so the DTO stays complete.
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    OrderItemDto toItemDto(OrderItem item);

    OrderDto toDto(Order order);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    AdminOrderDto toAdminDto(Order order);
}
