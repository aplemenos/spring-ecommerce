package com.aplemenos.ecommerce.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // @EntityGraph fetches items in the same query, avoiding N+1 without
    // making the association EAGER on the entity itself.
    @EntityGraph(attributePaths = "items")
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = "items")
    Optional<Order> findWithItemsById(Long id);

    // Admin view: every order, newest first.
    @EntityGraph(attributePaths = "items")
    List<Order> findAllByOrderByCreatedAtDesc();
}
