package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.*;

public interface cartrepository extends CrudRepository<carts, CartId> {

    @Query("SELECT c FROM carts c WHERE c.id.customerId = :customerId")
    List<carts> findByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT COUNT(c) > 0 FROM carts c WHERE c.id.productId = :productId")
    boolean existsByProductId(@Param("productId") Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM carts c WHERE c.id.customerId = :customerId")
    void deleteAllByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT c FROM carts c WHERE c.id.customerId = :customerId AND c.id.productId = :productId")
    carts findByCustomerIdAndProductId(@Param("customerId") int customerId, @Param("productId") int productId);
}
