package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productimages")
public class productotherimages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    private int ImageId;

    @ManyToOne 
    @JoinColumn(name = "productid", nullable = false) 
    private products product;

    @Column(name = "images", length = 2083)
    private String ProductImage;

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }


    public String getProductImage() {
        return ProductImage;
    }

    public void setProduct(products product) {
        this.product = product;
    }

    public products getProduct() {
        return product;
    }

    
}
