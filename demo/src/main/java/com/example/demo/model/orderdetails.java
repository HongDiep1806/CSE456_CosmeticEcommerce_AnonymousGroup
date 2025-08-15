package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orderdetails")
public class orderdetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id", length = 10, nullable = false)
    private int OrderDetailId;

    @ManyToOne
    @JoinColumn(name = "orderid", nullable = false)
    private orders order;

    @Column (name = "productid", length = 10, nullable = false)
    private int ProductId;

    @Column (name = "price", nullable = false)
    private double ProductPrice;

    @Column (name = "quantity", nullable = false)
    private int ProductQuantity;

    @Column (name = "coupon", length = 50)
    private String coupon;
    
    public int getOrderDetailId() {
        return OrderDetailId;
    }
    public void setOrderDetailId(int orderDetailId) {
        OrderDetailId = orderDetailId;
    }
    public orders getOrder() {
        return order;
    }
    public void setOrder(orders order) {
        this.order = order;
    }
    public int getProductId() {
        return ProductId;
    }
    public void setProductId(int productId) {
        ProductId = productId;
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
    public String getCoupon() {
        return coupon;
    }
    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

}
