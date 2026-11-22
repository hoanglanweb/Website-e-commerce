package com.example.electronics_store.Model;

import java.math.BigDecimal;

public class HomeProductDTO {
    private Long variantId;
    private String productName;
    private String slug;
    private String brandName;
    private String categoryName;
    private BigDecimal price;
    private String imageUrl;
    private String altText;

    public HomeProductDTO(Long variantId, String productName, String slug,
                          String brandName, String categoryName, BigDecimal price,
                          String imageUrl, String altText) {
        this.variantId = variantId;
        this.productName = productName;
        this.slug = slug;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    public Long getVariantId() { return variantId; }
    public String getProductName() { return productName; }
    public String getSlug() { return slug; }
    public String getBrandName() { return brandName; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getAltText() { return altText; }
}