package com.example.electronics_store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    public interface ProductReviewCountProjection {
        Long getProductId();
        Long getReviewCount();
    }

    @Query("""
        SELECT r.product.id AS productId, COUNT(r.id) AS reviewCount
        FROM ProductReview r
        GROUP BY r.product.id
    """)
    List<ProductReviewCountProjection> countReviewsGroupedByProduct();

    @Query("""
        SELECT r FROM ProductReview r
        JOIN FETCH r.user u
        WHERE r.product.id = :productId
        ORDER BY r.createdAt DESC, r.id DESC
    """)
    List<ProductReview> findByProductIdWithUser(@Param("productId") Long productId);
}