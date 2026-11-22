package com.example.electronics_store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductSpecification;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {

    @Query("""
        SELECT ps FROM ProductSpecification ps
        JOIN FETCH ps.group g
        WHERE ps.product.id = :productId
        ORDER BY g.displayOrder ASC, ps.displayOrder ASC
    """)
    List<ProductSpecification> findAllByProductIdSorted(@Param("productId") Long productId);
}