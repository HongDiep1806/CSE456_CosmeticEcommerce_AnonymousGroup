package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.orderdetails;

public interface orderdetailrepository extends CrudRepository<orderdetails, Integer> {
    @Query("SELECT o FROM orderdetails o WHERE o.order.OrderId = :orderId")
    List<orderdetails> findByOrderId(@Param("orderId") int orderId);
}
