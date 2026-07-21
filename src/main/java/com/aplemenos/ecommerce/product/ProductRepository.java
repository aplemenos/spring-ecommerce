package com.aplemenos.ecommerce.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    /**
     * Atomically decrements stock only if enough is available. Returns the number
     * of rows updated: 1 on success, 0 if stock was insufficient. Because the
     * "stock >= :qty" check and the subtraction happen in a single SQL statement,
     * concurrent checkouts cannot oversell — the database serializes the updates.
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty "
            + "WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);
}
