package com.aplemenos.ecommerce.product.mapper;

import com.aplemenos.ecommerce.product.Product;
import com.aplemenos.ecommerce.product.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);
}
