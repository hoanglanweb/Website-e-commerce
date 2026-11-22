package com.example.electronics_store.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity ánh xạ tới bảng product_variants.
 * Lưu các phiên bản cấu hình khác nhau của một sản phẩm.
 */
@Entity
@Table(name = "product_variants")
public class ProductVariant {

    // Khóa chính, tự động tăng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sản phẩm sở hữu phiên bản cấu hình này.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Mã SKU của phiên bản.
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    // Tên phiên bản cấu hình.
    @Column(name = "variant_name", nullable = false, length = 255)
    private String variantName;

    // Giá ghi đè nếu phiên bản có giá riêng.
    @Column(name = "price_override", precision = 15, scale = 2)
    private BigDecimal priceOverride;

    // Mô tả cấu hình tổng quan của phiên bản.
    @Column(name = "configuration_summary", length = 500)
    private String configurationSummary;

    // Chuỗi định danh cấu hình của phiên bản.
    @Column(name = "variant_signature", nullable = false, length = 1000)
    private String variantSignature;

    // Trạng thái phiên bản: 1 = hoạt động, 0 = không hoạt động.
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Thời điểm tạo bản ghi do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật do database quản lý.
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Số lượng tồn kho của phiên bản.
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    // Constructor mặc định dành cho JPA.
    public ProductVariant() {
    }

    // Getter và Setter.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public String getConfigurationSummary() {
        return configurationSummary;
    }

    public void setConfigurationSummary(String configurationSummary) {
        this.configurationSummary = configurationSummary;
    }

    public String getVariantSignature() {
        return variantSignature;
    }

    public void setVariantSignature(String variantSignature) {
        this.variantSignature = variantSignature;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
