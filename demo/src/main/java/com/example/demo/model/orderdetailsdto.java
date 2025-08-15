package com.example.demo.model;


public class orderdetailsdto {
    private int ProductId;
    private String ProductName;
    private String ProductMainImage;
    private int quantity;
    private double ProductPrice;
    private double totalPrice;
    public double getProductPrice() {
        return ProductPrice;
    }
    public void setProductPrice(double productPrice) {
        ProductPrice = productPrice;
    }
    public int getProductId() {
        return ProductId;
    }
    public void setProductId(int productId) {
        ProductId = productId;
    }
    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getProductMainImage() {
        return ProductMainImage;
    }
    public void setProductMainImage(String productMainImage) {
        ProductMainImage = productMainImage;
    }
}
