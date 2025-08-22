package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.*;
@Repository
public interface CartRepository extends CrudRepository<Carts, CartId> {

    @Query("SELECT c FROM Carts c WHERE c.id.customerId = :customerId")
    List<Carts> findByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT COUNT(c) > 0 FROM Carts c WHERE c.id.productId = :productId")
    boolean existsByProductId(@Param("productId") Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Carts c WHERE c.id.customerId = :customerId")
    void deleteAllByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT c FROM Carts c WHERE c.id.customerId = :customerId AND c.id.productId = :productId")
    Carts findByCustomerIdAndProductId(@Param("customerId") int customerId, @Param("productId") int productId);
}
