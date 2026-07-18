package com.aplemenos.ecommerce.product;

import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.product.dto.CreateProductRequest;
import com.aplemenos.ecommerce.product.dto.ProductDto;
import com.aplemenos.ecommerce.product.dto.UpdateProductRequest;
import com.aplemenos.ecommerce.product.mapper.ProductMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDto findById(Long id) {
        return productMapper.toDto(getProductOrThrow(id));
    }

    @Transactional
    public ProductDto create(CreateProductRequest request) {
        Category category = getCategoryOrThrow(request.categoryId());

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(Long id, UpdateProductRequest request) {
        Product product = getProductOrThrow(id);
        Category category = getCategoryOrThrow(request.categoryId());

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }
}
