package com.example.electronics_store.Model;

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
 * Entity ánh xạ tới bảng variant_images.
 * Quản lý danh sách hình ảnh của từng phiên bản sản phẩm.
 */
@Entity
@Table(name = "variant_images")
public class VariantImage {

    // Khóa chính.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Phiên bản sản phẩm sở hữu hình ảnh.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // Đường dẫn hình ảnh.
    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    // Văn bản thay thế khi hình ảnh không hiển thị.
    @Column(name = "alt_text", length = 255)
    private String altText;

    // Thứ tự hiển thị hình ảnh.
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // Đánh dấu hình ảnh đại diện của phiên bản.
    @Column(name = "is_primary", nullable = false)
    private Boolean primary = false;

    // Thời điểm tạo do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor mặc định dành cho JPA.
    public VariantImage() {
    }

    // Getter và Setter.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
