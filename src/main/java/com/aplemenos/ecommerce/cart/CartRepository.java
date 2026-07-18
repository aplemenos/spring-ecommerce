package com.aplemenos.ecommerce.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserEmail(String email);
}
