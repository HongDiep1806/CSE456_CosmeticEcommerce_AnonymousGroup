package com.example.demo.model;

public class categoriesdto {
    private int CategoryId;
    private String CategoryName;
    private int CategoryQuantity;
    private String CategoryStatus;

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

    public String getCategoryStatus() {
        return CategoryStatus;
    }

    public void setCategoryStatus(String categoryStatus) {
        CategoryStatus = categoryStatus;
    }

}
