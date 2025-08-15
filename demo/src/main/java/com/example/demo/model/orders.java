package com.example.demo.model;
import java.sql.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10, nullable = false)
    private int OrderId;

    @ManyToOne
    @JoinColumn(name = "customerid", nullable = false)
    private customers customer;

    @Column(name = "date", nullable = false)
    private Date OrderDate;

    public Date getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(Date orderDate) {
        OrderDate = orderDate;
    }

    @Column(name = "status", length = 50, nullable = false)
    private String OrderStatus;

    @Column(name = "amount", nullable = false)
    private double OrderAmount;

    @Column(name = "paymentmethod", length = 50, nullable = false)
    private String OrderPaymentMethod;

    @Column(name = "note", columnDefinition = "TEXT", length = 255)
    private String OrderNote;

    @Column(name = "address", length = 255, nullable = false)
    private String OrderAddress;

    @Column(name = "city", length = 50, nullable = false)
    private String OrderCity;

    @Column(name = "province", length = 50, nullable = false)
    private String OrderProvince;

    public double getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        OrderAmount = orderAmount;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
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

    public customers getCustomer() {
        return customer;
    }

    public void setCustomer(customers customer) {
        this.customer = customer;
    }

}
