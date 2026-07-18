package com.aplemenos.ecommerce.cart;

import com.aplemenos.ecommerce.cart.dto.CartDto;
import com.aplemenos.ecommerce.cart.mapper.CartMapper;
import com.aplemenos.ecommerce.common.exception.InsufficientStockException;
import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.product.Product;
import com.aplemenos.ecommerce.product.ProductRepository;
import com.aplemenos.ecommerce.user.User;
import com.aplemenos.ecommerce.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * All operations are scoped to the authenticated user's own cart. No cart id is
 * ever accepted from the client, so one user cannot touch another user's cart.
 */
@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public CartDto getCart(String email) {
        return cartMapper.toDto(getOrCreateCart(email));
    }

    @Transactional
    public CartDto addItem(String email, Long productId, int quantity) {
        Cart cart = getOrCreateCart(email);
        Product product = getProductOrThrow(productId);

        // Adding an existing product increments rather than duplicating the row
        int newQuantity = cart.findItemByProductId(productId)
                .map(item -> item.getQuantity() + quantity)
                .orElse(quantity);

        requireStock(product, newQuantity);

        cart.findItemByProductId(productId).ifPresentOrElse(
                item -> item.setQuantity(newQuantity),
                () -> cart.addItem(new CartItem(product, quantity)));

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto updateItemQuantity(String email, Long productId, int quantity) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cart.findItemByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product " + productId + " is not in the cart"));

        requireStock(item.getProduct(), quantity);
        item.setQuantity(quantity);

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto removeItem(String email, Long productId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cart.findItemByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product " + productId + " is not in the cart"));

        cart.removeItem(item);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto clear(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        return cartMapper.toDto(cartRepository.save(cart));
    }

    /** A cart is created lazily the first time the user touches it. */
    private Cart getOrCreateCart(String email) {
        return cartRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Authenticated user no longer exists: " + email));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", productId));
    }

    private void requireStock(Product product, int requested) {
        if (requested > product.getStock()) {
            throw new InsufficientStockException(
                    product.getName(), requested, product.getStock());
        }
    }
}
