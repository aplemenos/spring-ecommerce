package com.aplemenos.ecommerce.payment.mapper;

import com.aplemenos.ecommerce.payment.Payment;
import com.aplemenos.ecommerce.payment.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.id")
    PaymentDto toDto(Payment payment);
}
