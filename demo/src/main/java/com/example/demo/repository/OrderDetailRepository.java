package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.OrderDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetails, Integer> {
    @Query("SELECT o FROM OrderDetails o WHERE o.order.OrderId = :orderId")
    List<OrderDetails> findByOrderId(@Param("orderId") int orderId);
}
