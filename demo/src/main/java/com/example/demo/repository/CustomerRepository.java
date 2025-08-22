package com.example.demo.repository;

import com.example.demo.model.Customers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface CustomerRepository extends CrudRepository<Customers, Integer> {
    Customers findByCemail(String cemail);

    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM product", nativeQuery = true)
    int findNextCustomerId();

    @Modifying
    @Transactional
    @Query("UPDATE Customers c SET c.CustomerPassword = :newPassword WHERE c.cemail = :email")
    int updatePasswordByEmail(@Param("newPassword") String newPassword, @Param("email") String email);
    
    @Query("SELECT COUNT(o) FROM Customers o")
    Long countAllCustomers();

    @Query("SELECT p FROM Customers p ORDER BY p.CustomerId DESC")
    List<Customers> findTop5Customers(Pageable pageable);


}
