package com.example.demo.model;

import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class products {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    private int ProductId;

    @Column(name = "name", length = 2083)
    private String ProductName;

    @Column(name = "category", length = 30)
    private String ProductCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String ProductDescription;

    @Column(name = "mainimage", length = 2083)
    private String ProductMainImage;

    @Column(name = "price")
    private double ProductPrice;

    @Column(name = "quantity")
    private int ProductQuantity;

    @CreationTimestamp
    @Column(name = "createtime", updatable = false)
    private Date CreateTime;

    @Column(name = "status", length = 50)
    private String ProductStatus;

    public String getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(String productStatus) {
        ProductStatus = productStatus;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
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

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductMainImage() {
        return ProductMainImage;
    }

    public void setProductMainImage(String productMainImage) {
        ProductMainImage = productMainImage;
    }
    
    public double getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(double productPrice) {
        ProductPrice = productPrice;
    }

    public int getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        ProductQuantity = productQuantity;
    }
}
