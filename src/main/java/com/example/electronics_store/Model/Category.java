package com.example.electronics_store.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity Category
 *
 * Ánh xạ với bảng categories trong database electronics_store.
 *
 * Chức năng:
 * - Quản lý danh mục sản phẩm.
 * - Hỗ trợ danh mục cha và danh mục con.
 * - Lưu tên, slug, mô tả và hình ảnh danh mục.
 */
@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_categories_slug",
            columnNames = "slug"
        )
    }
)
public class Category {

    /**
     * Khóa chính của danh mục.
     *
     * Database: BIGINT UNSIGNED AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Danh mục cha.
     *
     * Database: parent_id BIGINT UNSIGNED NULL
     *
     * Nếu parent_id = NULL thì đây là danh mục cấp cao nhất.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Tên danh mục.
     *
     * Database: VARCHAR(150) NOT NULL
     */
    @Column(name = "category_name", nullable = false, length = 150)
    private String categoryName;

    /**
     * Đường dẫn thân thiện với SEO.
     *
     * Database: VARCHAR(180) NOT NULL UNIQUE
     */
    @Column(name = "slug", nullable = false, unique = true, length = 180)
    private String slug;

    /**
     * Mô tả danh mục.
     *
     * Database: TEXT NULL
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Đường dẫn hình ảnh đại diện danh mục.
     *
     * Database: VARCHAR(500) NULL
     */
    @Column(name = "image", length = 500)
    private String image;

    /**
     * Trạng thái hoạt động của danh mục.
     *
     * Database: TINYINT(1) NOT NULL DEFAULT 1
     */
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    /**
     * Danh sách danh mục con.
     *
     * mappedBy = "parent" cho biết quan hệ được quản lý
     * bởi thuộc tính parent ở phía Category con.
     *
     * Không khai báo CascadeType.ALL để tránh tự động
     * xóa hoặc thay đổi các danh mục liên quan.
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    /**
     * Thời điểm tạo danh mục.
     *
     * Database tự thiết lập DEFAULT CURRENT_TIMESTAMP.
     */
    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Thời điểm cập nhật danh mục gần nhất.
     *
     * Database tự cập nhật ON UPDATE CURRENT_TIMESTAMP.
     */
    @Column(
        name = "updated_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Constructor mặc định dành cho JPA.
     */
    public Category() {
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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
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
