package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class categories {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    private int CategoryId;

    @Column(name = "name", length = 50)
    private String CategoryName;

    @Column(name = "quantity")
    @Transient
    private int CategoryQuantity;

    @Column(name = "status")
    private String CategoryStatus;

    @Column(name = "isActive")
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getCategoryStatus() {
        return CategoryStatus;
    }

    public void setCategoryStatus(String categoryStatus) {
        CategoryStatus = categoryStatus;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }


    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public int getCategoryQuantity() {
        return CategoryQuantity;
    }

    public void setCategoryQuantity(int categoryQuantity) {
        CategoryQuantity = categoryQuantity;
    }
}
