package com.example.electronics_store.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_description_sections")
public class ProductDescriptionSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_id", nullable = false)
    private ProductDescription productDescription;

    @Column(name = "section_title", length = 255)
    private String sectionTitle;

    @Column(name = "section_content", columnDefinition = "LONGTEXT")
    private String sectionContent;

    @Column(name = "section_image", columnDefinition = "TEXT")
    private String sectionImage;

    @Column(name = "display_order")
    private Integer displayOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductDescription getProductDescription() { return productDescription; }
    public void setProductDescription(ProductDescription productDescription) { this.productDescription = productDescription; }

    public String getSectionTitle() { return sectionTitle; }
    public void setSectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; }

    public String getSectionContent() { return sectionContent; }
    public void setSectionContent(String sectionContent) { this.sectionContent = sectionContent; }

    public String getSectionImage() { return sectionImage; }
    public void setSectionImage(String sectionImage) { this.sectionImage = sectionImage; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}