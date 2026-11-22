package com.example.electronics_store.Model;

public class ProductActivityDTO {

    private Long productId;
    private String productName;
    private String formattedTime;

    public ProductActivityDTO(Long productId, String productName, String formattedTime) {
        this.productId = productId;
        this.productName = productName;
        this.formattedTime = formattedTime;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getFormattedTime() {
        return formattedTime;
    }
}