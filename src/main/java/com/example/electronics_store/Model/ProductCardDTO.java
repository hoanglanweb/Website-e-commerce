package com.example.electronics_store.Model;

import java.math.BigDecimal;

/**
 * DTO chứa dữ liệu hiển thị thẻ sản phẩm ngoài trang chủ.
 */
public class ProductCardDTO {
    private final Long productId;
    private final Long variantId;
    private final String productCode;
    private final String productName;
    private final String slug;
    private final String configurationSummary;
    private final String brandName;
    private final String categoryName;
    private final BigDecimal price;
    private final String imageUrl;
    private final String altText;
    private Integer stockQuantity;
    private Long reviewCount = 0L;

    public ProductCardDTO(Long productId, Long variantId, String productCode,
                          String productName, String slug, String configurationSummary,
                          String brandName, String categoryName,
                          BigDecimal price, String imageUrl, String altText) {
        this.productId = productId;
        this.variantId = variantId;
        this.productCode = productCode;
        this.productName = productName;
        this.slug = slug;
        this.configurationSummary = configurationSummary;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    public Long getProductId() { return productId; }
    public Long getVariantId() { return variantId; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public String getSlug() { return slug; }
    public String getConfigurationSummary() { return configurationSummary; }
    public String getBrandName() { return brandName; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getAltText() { return altText; }
    public Long getReviewCount() { return reviewCount; }
    public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
    public Integer getStockQuantity() {
    return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}