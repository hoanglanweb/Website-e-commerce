
package com.example.electronics_store.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity Brand
 *
 * Ánh xạ với bảng brands trong database electronics_store.
 *
 * Chức năng:
 * - Lưu thông tin thương hiệu laptop.
 * - Quản lý tên, slug, logo và mô tả thương hiệu.
 * - Theo dõi trạng thái hoạt động và thời gian tạo/cập nhật.
 */
@Entity
@Table(name = "brands")
public class Brand {

    /**
     * Khóa chính của thương hiệu.
     *
     * Database: BIGINT UNSIGNED AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên thương hiệu.
     *
     * Database: VARCHAR(150) NOT NULL
     */
    @Column(name = "brand_name", nullable = false, length = 150)
    private String brandName;

    /**
     * Đường dẫn thân thiện với SEO.
     *
     * Database: VARCHAR(180) NOT NULL UNIQUE
     */
    @Column(name = "slug", nullable = false, unique = true, length = 180)
    private String slug;

    /**
     * Đường dẫn hình ảnh logo thương hiệu.
     *
     * Database: VARCHAR(500) NULL
     */
    @Column(name = "logo", length = 500)
    private String logo;

    /**
     * Mô tả thương hiệu.
     *
     * Database: TEXT NULL
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Trạng thái hoạt động của thương hiệu.
     *
     * Database: TINYINT(1) NOT NULL DEFAULT 1
     *
     * true  = Đang hoạt động
     * false = Ngừng hoạt động
     */
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    /**
     * Thời điểm tạo thương hiệu.
     *
     * Database: DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
     *
     * Để MariaDB tự quản lý giá trị mặc định.
     */
    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Thời điểm cập nhật thương hiệu gần nhất.
     *
     * Database: DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
     * ON UPDATE CURRENT_TIMESTAMP
     *
     * Để MariaDB tự cập nhật thời gian.
     */
    @Column(
        name = "updated_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Constructor mặc định.
     * JPA yêu cầu constructor không tham số.
     */
    public Brand() {
    }

    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
