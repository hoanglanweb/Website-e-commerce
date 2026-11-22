package com.example.electronics_store.Model;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long itemId;
    private Long variantId;
    private Long productId;
    private String brandName;
    private String productName;
    private String slug;
    private String sku;
    private String variantName;
    private String configurationSummary;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal unitPrice;
    private int quantity;

    public CartItemDTO(Long itemId, Long variantId, Long productId, String brandName,
                       String productName, String slug, String sku, String variantName,
                       String configurationSummary, String imageUrl,
                       BigDecimal originalPrice, BigDecimal unitPrice, int quantity) {
        this.itemId = itemId;
        this.variantId = variantId;
        this.productId = productId;
        this.brandName = brandName;
        this.productName = productName;
        this.slug = slug;
        this.sku = sku;
        this.variantName = variantName;
        this.configurationSummary = configurationSummary;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice != null ? unitPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }

    public BigDecimal getSavingsPerUnit() {
        if (originalPrice != null && unitPrice != null && originalPrice.compareTo(unitPrice) > 0) {
            return originalPrice.subtract(unitPrice);
        }
        return BigDecimal.ZERO;
    }

    // Getters & Setters
    public Long getItemId() { return itemId; }
    public Long getVariantId() { return variantId; }
    public Long getProductId() { return productId; }
    public String getBrandName() { return brandName; }
    public String getProductName() { return productName; }
    public String getSlug() { return slug; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public String getConfigurationSummary() { return configurationSummary; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}