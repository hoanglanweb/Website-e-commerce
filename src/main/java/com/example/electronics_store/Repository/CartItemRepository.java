package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
        SELECT ci FROM CartItem ci
        JOIN FETCH ci.variant v
        JOIN FETCH v.product p
        LEFT JOIN FETCH p.brand b
        WHERE ci.cart.id = :cartId
        ORDER BY ci.addedAt DESC
    """)
    List<CartItem> findAllDetailedByCartId(@Param("cartId") Long cartId);

    Optional<CartItem> findByCartIdAndVariantId(Long cartId, Long variantId);

    void deleteByCartId(Long cartId);
}