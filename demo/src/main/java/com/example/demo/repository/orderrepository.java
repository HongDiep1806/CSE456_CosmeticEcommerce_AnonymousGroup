package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.orders;

public interface orderrepository extends CrudRepository<orders, Integer> {
    @Query("SELECT o FROM orders o WHERE o.customer.CustomerId = :customerId")
    List<orders> findOrdersByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT SUM(o.OrderAmount) FROM orders o")
    Long findTotalOrderAmount();

    @Query("SELECT COUNT(o) FROM orders o")
    Long countAllOrders();

    @Query("SELECT p FROM orders p ORDER BY p.OrderDate DESC")
    List<orders> findTop50rders(Pageable pageable);

}
