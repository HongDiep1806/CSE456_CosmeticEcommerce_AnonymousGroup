package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cart")
public class carts implements Serializable {

    @EmbeddedId
    private CartId id;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private customers customer;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private products product;

    @Column(nullable = false)
    private int quantity;

    public CartId getId() {
        return id;
    }

    public void setId(CartId id) {
        this.id = id;
    }

    public customers getCustomer() {
        return customer;
    }

    public void setCustomer(customers customer) {
        this.customer = customer;
    }

    public products getProduct() {
        return product;
    }

    public void setProduct(products product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
