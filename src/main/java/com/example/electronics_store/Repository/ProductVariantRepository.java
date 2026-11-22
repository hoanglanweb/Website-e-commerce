package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    List<ProductVariant> findByProductIdAndStatus(Long productId, Boolean status);

    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByVariantNameContainingIgnoreCase(String variantName);

    Optional<ProductVariant> findByVariantSignature(String variantSignature);

    boolean existsByVariantSignature(String variantSignature);

    List<ProductVariant> findByProductIdAndStatusOrderByCreatedAtDesc(Long productId, Boolean status);

    List<ProductVariant> findByStatus(Boolean status);

    List<ProductVariant> findAllByOrderByCreatedAtDesc();

    List<ProductVariant> findByProductIdAndStatusTrueOrderByIdAsc(Long productId);

    // 1. Bản không tham số (dùng cho các service khác như gợi ý sản phẩm)
    @Query("""
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                ORDER BY pv.id ASC
            """)
    List<ProductCardDTO> findAllActiveProductCards();

    // 2. Bản có Pageable (dùng cho phân trang ngoài Trang chủ)
    @Query(value = """
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                ORDER BY pv.id ASC
            """, countQuery = """
                SELECT COUNT(pv.id)
                FROM ProductVariant pv
                JOIN pv.product p
                WHERE p.status = true AND pv.status = true
            """)
    Page<ProductCardDTO> findAllActiveProductCards(Pageable pageable);

    // 3. Lọc biến thể theo thương hiệu có phân trang
    @Query(value = """
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                  AND UPPER(b.brandName) = UPPER(:brandName)
                ORDER BY pv.id ASC
            """, countQuery = """
                SELECT COUNT(pv.id)
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                WHERE p.status = true AND pv.status = true
                  AND UPPER(b.brandName) = UPPER(:brandName)
            """)
    Page<ProductCardDTO> findActiveProductCardsByBrand(@Param("brandName") String brandName, Pageable pageable);

    // Lọc tổng hợp theo Thương hiệu (tùy chọn) + Danh mục (tùy chọn) có phân trang
    @Query(value = """
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
                ORDER BY pv.id ASC
            """, countQuery = """
                SELECT COUNT(pv.id)
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
            """)
    Page<ProductCardDTO> searchActiveProductCards(
            @Param("brandName") String brandName,
            @Param("categoryName") String categoryName,
            Pageable pageable);

    // Lọc tổng hợp theo Thương hiệu + Danh mục + Từ khóa tìm kiếm có phân trang
    @Query(value = """
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
                  AND (:keyword IS NULL
                       OR UPPER(p.productName) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.productCode) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.configurationSummary) LIKE CONCAT('%', UPPER(:keyword), '%'))
                ORDER BY pv.id ASC
            """, countQuery = """
                SELECT COUNT(pv.id)
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
                  AND (:keyword IS NULL
                       OR UPPER(p.productName) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.productCode) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.configurationSummary) LIKE CONCAT('%', UPPER(:keyword), '%'))
            """)
    Page<ProductCardDTO> searchActiveProductCards(
            @Param("brandName") String brandName,
            @Param("categoryName") String categoryName,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Gợi ý tìm kiếm nhanh (Autocomplete) kèm hình ảnh và giá
    @Query("""
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    p.configurationSummary,
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                  AND (UPPER(p.productName) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.productCode) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(p.configurationSummary) LIKE CONCAT('%', UPPER(:keyword), '%')
                       OR UPPER(b.brandName) LIKE CONCAT('%', UPPER(:keyword), '%'))
                ORDER BY pv.id ASC
            """)
    List<ProductCardDTO> findTopSuggestions(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm thông minh theo linh kiện GPU / CPU / Tên máy (hỗ trợ tối đa 3 từ
    // khóa cốt lõi)
    // Tìm kiếm thông minh + Lọc + Sắp xếp giá động qua Pageable
    @Query(value = """
                SELECT new com.example.electronics_store.Model.ProductCardDTO(
                    p.id,
                    pv.id,
                    p.productCode,
                    p.productName,
                    p.slug,
                    COALESCE(pv.configurationSummary, p.configurationSummary),
                    b.brandName,
                    c.categoryName,
                    COALESCE(pv.priceOverride, p.basePrice),
                    vi.imageUrl,
                    vi.altText
                )
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                LEFT JOIN VariantImage vi ON vi.variant.id = pv.id AND vi.primary = true
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
                  AND (:kw1 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw1, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw1, '%')
                        )
                  ))
                  AND (:kw2 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw2, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw2, '%')
                        )
                  ))
                  AND (:kw3 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw3, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw3, '%')
                        )
                  ))
            """, countQuery = """
                SELECT COUNT(pv.id)
                FROM ProductVariant pv
                JOIN pv.product p
                JOIN p.brand b
                JOIN p.category c
                WHERE p.status = true AND pv.status = true
                  AND (:brandName IS NULL OR UPPER(b.brandName) = UPPER(:brandName))
                  AND (:categoryName IS NULL OR UPPER(c.categoryName) LIKE CONCAT('%', UPPER(:categoryName), '%'))
                  AND (:kw1 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw1, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw1, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw1, '%')
                        )
                  ))
                  AND (:kw2 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw2, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw2, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw2, '%')
                        )
                  ))
                  AND (:kw3 IS NULL OR (
                        UPPER(p.productName) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(COALESCE(pv.configurationSummary, '')) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(COALESCE(p.configurationSummary, '')) LIKE CONCAT('%', :kw3, '%')
                        OR UPPER(b.brandName) LIKE CONCAT('%', :kw3, '%')
                        OR EXISTS (
                            SELECT 1 FROM ProductSpecification s
                            WHERE s.product.id = p.id AND UPPER(s.specValue) LIKE CONCAT('%', :kw3, '%')
                        )
                  ))
            """)
    Page<ProductCardDTO> searchSmartProductCards(
            @Param("brandName") String brandName,
            @Param("categoryName") String categoryName,
            @Param("kw1") String kw1,
            @Param("kw2") String kw2,
            @Param("kw3") String kw3,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(pv.stockQuantity), 0)
            FROM ProductVariant pv
            WHERE pv.product.id = :productId
              AND pv.status = true
            """)
    Integer sumStockQuantityByProductId(@Param("productId") Long productId);

    @Query("""
            SELECT pv.id AS variantId, COALESCE(pv.stockQuantity, 0) AS stockQuantity
            FROM ProductVariant pv
            WHERE pv.status = true
            """)
    List<VariantStockProjection> findActiveVariantStocks();

    interface VariantStockProjection {
        Long getVariantId();

        Integer getStockQuantity();
    }
}