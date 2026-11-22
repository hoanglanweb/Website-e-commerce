package com.example.electronics_store.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDetailDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private String slug;
    private String configurationSummary;
    private String brandName;
    private String categoryName;
    private BigDecimal basePrice;
    private Integer stockQuantity;

    // Nội dung từ bảng product_descriptions
    private String shortDescription;
    private String introductionTitle;
    private String introduction;
    private List<String> highlightList = Collections.emptyList();

    // Các mục nội dung chi tiết từ bảng product_description_sections
    private List<SectionItemDTO> sections = Collections.emptyList();

    // Thông số kỹ thuật từ bảng product_specifications
    private List<SpecItemDTO> highlightSpecs = Collections.emptyList();
    private List<SpecGroupDTO> specGroups = Collections.emptyList();

    // Thông tin biến thể đang chọn
    private Long selectedVariantId;
    private String selectedSku;
    private String selectedVariantName;
    private String selectedConfiguration;
    private BigDecimal displayPrice;

    private List<VariantOptionDTO> variants = Collections.emptyList();
    private List<ImageItemDTO> images = Collections.emptyList();
    private List<ImageItemDTO> extraImages = Collections.emptyList();
    private List<ProductCardDTO> recommendedProducts = Collections.emptyList();
    private List<ProductCardDTO> relatedProducts = new ArrayList<>();

    public static class VariantOptionDTO {
        private Long variantId;
        private String sku;
        private String variantName;
        private String configurationSummary;
        private BigDecimal price;

        public VariantOptionDTO(Long variantId, String sku, String variantName,
                String configurationSummary, BigDecimal price) {
            this.variantId = variantId;
            this.sku = sku;
            this.variantName = variantName;
            this.configurationSummary = configurationSummary;
            this.price = price;
        }

        public Long getVariantId() {
            return variantId;
        }

        public String getSku() {
            return sku;
        }

        public String getVariantName() {
            return variantName;
        }

        public String getConfigurationSummary() {
            return configurationSummary;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }

    public static class ImageItemDTO {
        private String imageUrl;
        private String altText;
        private boolean primary;

        public ImageItemDTO(String imageUrl, String altText, boolean primary) {
            this.imageUrl = imageUrl;
            this.altText = altText;
            this.primary = primary;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getAltText() {
            return altText;
        }

        public boolean isPrimary() {
            return primary;
        }
    }

    public static class SectionItemDTO {
        private String sectionTitle;
        private String sectionContent;
        private String sectionImage;

        public SectionItemDTO(String sectionTitle, String sectionContent, String sectionImage) {
            this.sectionTitle = sectionTitle;
            this.sectionContent = sectionContent;
            this.sectionImage = sectionImage;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public String getSectionContent() {
            return sectionContent;
        }

        public String getSectionImage() {
            return sectionImage;
        }
    }

    public static class SpecItemDTO {
        private String name;
        private String value;

        public SpecItemDTO(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public static class SpecGroupDTO {
        private String groupName;
        private List<SpecItemDTO> items;

        public SpecGroupDTO(String groupName, List<SpecItemDTO> items) {
            this.groupName = groupName;
            this.items = items;
        }

        public String getGroupName() {
            return groupName;
        }

        public List<SpecItemDTO> getItems() {
            return items;
        }
    }

    // Getters & Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getConfigurationSummary() {
        return configurationSummary;
    }

    public void setConfigurationSummary(String configurationSummary) {
        this.configurationSummary = configurationSummary;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
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

    public List<String> getHighlightList() {
        return highlightList;
    }

    public void setHighlightList(List<String> highlightList) {
        this.highlightList = highlightList;
    }

    public List<SectionItemDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionItemDTO> sections) {
        this.sections = sections;
    }

    public List<SpecItemDTO> getHighlightSpecs() {
        return highlightSpecs;
    }

    public void setHighlightSpecs(List<SpecItemDTO> highlightSpecs) {
        this.highlightSpecs = highlightSpecs;
    }

    public List<SpecGroupDTO> getSpecGroups() {
        return specGroups;
    }

    public void setSpecGroups(List<SpecGroupDTO> specGroups) {
        this.specGroups = specGroups;
    }

    public Long getSelectedVariantId() {
        return selectedVariantId;
    }

    public void setSelectedVariantId(Long selectedVariantId) {
        this.selectedVariantId = selectedVariantId;
    }

    public String getSelectedSku() {
        return selectedSku;
    }

    public void setSelectedSku(String selectedSku) {
        this.selectedSku = selectedSku;
    }

    public String getSelectedVariantName() {
        return selectedVariantName;
    }

    public void setSelectedVariantName(String selectedVariantName) {
        this.selectedVariantName = selectedVariantName;
    }

    public String getSelectedConfiguration() {
        return selectedConfiguration;
    }

    public void setSelectedConfiguration(String selectedConfiguration) {
        this.selectedConfiguration = selectedConfiguration;
    }

    public BigDecimal getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(BigDecimal displayPrice) {
        this.displayPrice = displayPrice;
    }

    public List<VariantOptionDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantOptionDTO> variants) {
        this.variants = variants;
    }

    public List<ImageItemDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageItemDTO> images) {
        this.images = images;
    }

    public List<ImageItemDTO> getExtraImages() {
        return extraImages;
    }

    public void setExtraImages(List<ImageItemDTO> extraImages) {
        this.extraImages = extraImages;
    }

    public List<ProductCardDTO> getRecommendedProducts() {
        return recommendedProducts;
    }

    public void setRecommendedProducts(List<ProductCardDTO> recommendedProducts) {
        this.recommendedProducts = recommendedProducts;
    }

    public List<ProductCardDTO> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<ProductCardDTO> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }
    public Integer getStockQuantity() {
    return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}