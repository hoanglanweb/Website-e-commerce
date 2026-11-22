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
@Table(name = "product_specifications")
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private SpecificationGroup group;

    @Column(name = "spec_name", length = 150, nullable = false)
    private String specName;

    @Column(name = "spec_value", length = 500, nullable = false)
    private String specValue;

    @Column(name = "is_highlight", nullable = false)
    private Boolean highlight = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public SpecificationGroup getGroup() { return group; }
    public void setGroup(SpecificationGroup group) { this.group = group; }

    public String getSpecName() { return specName; }
    public void setSpecName(String specName) { this.specName = specName; }

    public String getSpecValue() { return specValue; }
    public void setSpecValue(String specValue) { this.specValue = specValue; }

    public Boolean getHighlight() { return highlight; }
    public void setHighlight(Boolean highlight) { this.highlight = highlight; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}