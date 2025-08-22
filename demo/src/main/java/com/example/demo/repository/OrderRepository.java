package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o WHERE o.customer.CustomerId = :customerId")
    List<Orders> findOrdersByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT SUM(o.OrderAmount) FROM Orders o")
    Long findTotalOrderAmount();

    @Query("SELECT COUNT(o) FROM Orders o")
    Long countAllOrders();

    @Query("SELECT p FROM Orders p ORDER BY p.OrderDate DESC")
    List<Orders> findTop50rders(Pageable pageable);

}
