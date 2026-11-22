
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
 * Entity ánh xạ tới bảng product_descriptions.
 * Lưu nội dung mô tả chi tiết của sản phẩm.
 */
@Entity
@Table(name = "product_descriptions")
public class ProductDescription {

    // Khóa chính.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sản phẩm sở hữu nội dung mô tả.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Mô tả ngắn của sản phẩm.
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    // Tiêu đề phần giới thiệu.
    @Column(name = "introduction_title", length = 255)
    private String introductionTitle;

    // Nội dung giới thiệu chi tiết.
    @Column(name = "introduction", columnDefinition = "LONGTEXT")
    private String introduction;

    // Dữ liệu JSON chứa các điểm nổi bật của sản phẩm.
    @Column(name = "highlights", columnDefinition = "LONGTEXT")
    private String highlights;

    // Thời điểm tạo do database quản lý.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật do database quản lý.
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Constructor mặc định dành cho JPA.
    public ProductDescription() {
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getIntroductionTitle() {
        return introductionTitle;
    }

    public void setIntroductionTitle(String introductionTitle) {
        this.introductionTitle = introductionTitle;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
