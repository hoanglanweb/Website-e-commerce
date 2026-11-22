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
 * Entity ánh xạ tới bảng products trong database electronics_store.
 */
@Entity
@Table(name = "products")
public class Product {

    // Khóa chính, tự động tăng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Danh mục sản phẩm.
    // category_id là khóa ngoại tham chiếu categories.id.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Thương hiệu sản phẩm.
    // brand_id là khóa ngoại tham chiếu brands.id.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    // Mã sản phẩm.
    @Column(name = "product_code", nullable = false, length = 100)
    private String productCode;

    // Tên sản phẩm.
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    // Đường dẫn thân thiện với SEO.
    @Column(name = "slug", nullable = false, length = 255)
    private String slug;

    // Giá cơ bản của sản phẩm.
    // Sử dụng BigDecimal để đảm bảo độ chính xác khi xử lý tiền tệ.
    @Column(name = "base_price", precision = 15, scale = 2)
    private BigDecimal basePrice;

    // Thông tin cấu hình tổng quan.
    @Column(name = "configuration_summary", length = 500)
    private String configurationSummary;

    // Trạng thái sản phẩm: 1 = hoạt động, 0 = không hoạt động.
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Thời điểm tạo bản ghi, do database tự thiết lập.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật, do database tự thiết lập và cập nhật.
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    // Constructor mặc định dành cho JPA.
    public Product() {
    }

    // Getter và Setter.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getConfigurationSummary() {
        return configurationSummary;
    }

    public void setConfigurationSummary(String configurationSummary) {
        this.configurationSummary = configurationSummary;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
