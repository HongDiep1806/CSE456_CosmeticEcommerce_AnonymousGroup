package com.example.demo.service;

import com.example.demo.model.customers;
import com.example.demo.model.orders;
import com.example.demo.model.products;
import com.example.demo.repository.customerrepository;
import com.example.demo.repository.orderrepository;
import com.example.demo.repository.productrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired private orderrepository orderrepo;
    @Autowired private customerrepository customerrepo;
    @Autowired private productrepository productrepo;

    public Long getTotalOrderAmount() {
        return orderrepo.findTotalOrderAmount();
    }

    public Long getTotalOrders() {
        return orderrepo.countAllOrders();
    }

    public Long getTotalCustomers() {
        return customerrepo.countAllCustomers();
    }

    public List<products> getBestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productrepo.findBestProducts(pageable);
    }

    public List<customers> getTopCustomers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return customerrepo.findTop5Customers(pageable);
    }

    public List<orders> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderrepo.findTop50rders(pageable);
    }
}
