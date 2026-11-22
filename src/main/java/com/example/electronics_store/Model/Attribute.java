package com.example.electronics_store.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity ánh xạ tới bảng attributes.
 * Lưu định nghĩa các thuộc tính kỹ thuật của sản phẩm.
 */
@Entity
@Table(name = "attributes")
public class Attribute {

    // Khóa chính, tự động tăng.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên thuộc tính, ví dụ: CPU, GPU, RAM, SSD.
    @Column(name = "attribute_name", nullable = false, length = 150)
    private String attributeName;

    // Mã định danh thuộc tính, ví dụ: cpu, gpu, ram, ssd.
    @Column(name = "attribute_code", nullable = false, length = 150)
    private String attributeCode;

    // Kiểu dữ liệu của thuộc tính.
    // Các giá trị hợp lệ: SELECT, TEXT, NUMBER, BOOLEAN, DATE.
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType = DataType.TEXT;

    // Mô tả chi tiết thuộc tính.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Trạng thái: true = hoạt động, false = không hoạt động.
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Thời điểm tạo bản ghi do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật do database quản lý.
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Enum ánh xạ đúng các giá trị ENUM trong database.
    public enum DataType {
        SELECT,
        TEXT,
        NUMBER,
        BOOLEAN,
        DATE
    }

    // Constructor mặc định dành cho JPA.
    public Attribute() {
    }

    // Getter và Setter.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}

