package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {

    @Query("SELECT COUNT(a) > 0 FROM Admin a WHERE a.AdminEmail = ?1")
    boolean existsByEmail(String email);

    @Query("SELECT a FROM Admin a WHERE a.AdminEmail = ?1")
    Admin findByAdminEmail(String email);

    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM admins", nativeQuery = true)
    int findNextAdminId();
}
