
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
 * Entity ánh xạ tới bảng attribute_values.
 * Lưu các giá trị cụ thể của từng thuộc tính kỹ thuật.
 */
@Entity
@Table(name = "attribute_values")
public class AttributeValue {

    // Khóa chính, tự động tăng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thuộc tính mà giá trị này trực thuộc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    // Giá trị lưu trong database, ví dụ: 16GB, Core 7 150U.
    @Column(name = "value", nullable = false, length = 255)
    private String value;

    // Giá trị hiển thị trên giao diện.
    @Column(name = "display_value", length = 255)
    private String displayValue;

    // Đơn vị của giá trị, ví dụ: GB, GHz.
    @Column(name = "unit", length = 50)
    private String unit;

    // Thứ tự hiển thị.
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // Trạng thái: true = hoạt động, false = không hoạt động.
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Thời điểm tạo bản ghi do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor mặc định dành cho JPA.
    public AttributeValue() {
    }

    // Getter và Setter.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
