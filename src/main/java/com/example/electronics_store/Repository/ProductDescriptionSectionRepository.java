package com.example.electronics_store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductDescriptionSection;

@Repository
public interface ProductDescriptionSectionRepository extends JpaRepository<ProductDescriptionSection, Long> {

    List<ProductDescriptionSection> findByProductDescriptionIdOrderByDisplayOrderAscIdAsc(Long descriptionId);
}