
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
 * Entity ánh xạ tới bảng category_attribute_overrides.
 * Lưu các cấu hình ghi đè thuộc tính theo từng danh mục.
 */
@Entity
@Table(name = "category_attribute_overrides")
public class CategoryAttributeOverride {

    // Khóa chính.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Danh mục được áp dụng cấu hình ghi đè.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Thuộc tính được ghi đè cấu hình.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    // Bật hoặc tắt thuộc tính tại danh mục này.
    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = true;

    // Các trường nullable: null nghĩa là không ghi đè cấu hình gốc.
    @Column(name = "is_variant_option")
    private Boolean variantOption;

    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "is_filterable")
    private Boolean filterable;

    @Column(name = "is_visible")
    private Boolean visible;

    @Column(name = "allow_multiple")
    private Boolean allowMultiple;

    // Nhóm hiển thị thuộc tính, nếu có ghi đè.
    @Column(name = "group_name", length = 150)
    private String groupName;

    // Thứ tự hiển thị, có thể để null để dùng cấu hình gốc.
    @Column(name = "display_order")
    private Integer displayOrder;

    // Thời điểm tạo do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật do database tự động quản lý.
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Constructor mặc định dành cho JPA.
    public CategoryAttributeOverride() {
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

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getVariantOption() {
        return variantOption;
    }

    public void setVariantOption(Boolean variantOption) {
        this.variantOption = variantOption;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getFilterable() {
        return filterable;
    }

    public void setFilterable(Boolean filterable) {
        this.filterable = filterable;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
