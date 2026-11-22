
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
 * Entity ánh xạ tới bảng category_attributes.
 * Quản lý các thuộc tính được áp dụng cho từng danh mục sản phẩm.
 */
@Entity
@Table(name = "category_attributes")
public class CategoryAttribute {

    // Khóa chính, tự động tăng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Danh mục sản phẩm áp dụng thuộc tính.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Thuộc tính kỹ thuật được gán cho danh mục.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    // Cho biết thuộc tính có được dùng làm tùy chọn phiên bản hay không.
    @Column(name = "is_variant_option", nullable = false)
    private Boolean variantOption = false;

    // Cho biết thuộc tính có bắt buộc nhập hay không.
    @Column(name = "is_required", nullable = false)
    private Boolean required = false;

    // Cho biết thuộc tính có thể dùng để lọc sản phẩm hay không.
    @Column(name = "is_filterable", nullable = false)
    private Boolean filterable = false;

    // Cho biết thuộc tính có được hiển thị hay không.
    @Column(name = "is_visible", nullable = false)
    private Boolean visible = true;

    // Cho biết thuộc tính có cho phép nhiều giá trị hay không.
    @Column(name = "allow_multiple", nullable = false)
    private Boolean allowMultiple = false;

    // Tên nhóm hiển thị thuộc tính.
    @Column(name = "group_name", length = 150)
    private String groupName;

    // Thứ tự hiển thị thuộc tính.
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // Trạng thái: true = hoạt động, false = không hoạt động.
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Thời điểm tạo bản ghi do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor mặc định dành cho JPA.
    public CategoryAttribute() {
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
