package com.example.demo.model;

public class ordersdto {
    private int OrderId;
    private int CustomerId;
    private String OrderDate;
    private String OrderStatus;
    private int OrderAmount;
    private String OrderPaymentMethod;
    private String OrderNote;
    private String OrderAddress;
    private String OrderCity;
    private String OrderProvince;

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public int getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        OrderAmount = orderAmount;
    }

    public String getOrderPaymentMethod() {
        return OrderPaymentMethod;
    }

    public void setOrderPaymentMethod(String orderPaymentMethod) {
        OrderPaymentMethod = orderPaymentMethod;
    }

    public String getOrderNote() {
        return OrderNote;
    }

    public void setOrderNote(String orderNote) {
        OrderNote = orderNote;
    }

    public String getOrderAddress() {
        return OrderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        OrderAddress = orderAddress;
    }

    public String getOrderCity() {
        return OrderCity;
    }

    public void setOrderCity(String orderCity) {
        OrderCity = orderCity;
    }

    public String getOrderProvince() {
        return OrderProvince;
    }

    public void setOrderProvince(String orderProvince) {
        OrderProvince = orderProvince;
    }
}
