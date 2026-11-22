
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
 * Entity ánh xạ tới bảng variant_attribute_values.
 * Lưu các giá trị thuộc tính riêng của từng phiên bản sản phẩm.
 */
@Entity
@Table(name = "variant_attribute_values")
public class VariantAttributeValue {

    // Khóa chính.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Phiên bản sản phẩm sở hữu giá trị thuộc tính này.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // Thuộc tính kỹ thuật được gán cho phiên bản.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    // Giá trị được chọn từ danh sách attribute_values.
    // Có thể null nếu sử dụng customValue.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id")
    private AttributeValue attributeValue;

    // Giá trị tùy chỉnh dạng văn bản.
    @Column(name = "custom_value", columnDefinition = "TEXT")
    private String customValue;

    // Thứ tự hiển thị giá trị thuộc tính.
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // Thời điểm tạo do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật do database quản lý.
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Constructor mặc định dành cho JPA.
    public VariantAttributeValue() {
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

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(AttributeValue attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getCustomValue() {
        return customValue;
    }

    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
