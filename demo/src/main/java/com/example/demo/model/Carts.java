package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cart")
public class Carts implements Serializable {

    @EmbeddedId
    private CartId id;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private Customers customer;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Products product;

    @Column(nullable = false)
    private int quantity;

    public CartId getId() {
        return id;
    }

    public void setId(CartId id) {
        this.id = id;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
