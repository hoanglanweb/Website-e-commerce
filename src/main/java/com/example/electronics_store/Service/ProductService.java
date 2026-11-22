package com.example.electronics_store.Service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Model.ProductDetailDTO;

public interface ProductService {
    List<ProductCardDTO> getAllActiveProductCards();

    ProductDetailDTO getProductDetailBySlug(String slug, Long variantId);

    Page<ProductCardDTO> searchProducts(
            String brand,
            String category,
            String keyword,
            String sort,
            int page,
            int size);

    List<ProductCardDTO> getSearchSuggestions(String keyword);
}