package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.admin;

public interface adminrepository extends CrudRepository<admin, Integer> {

    @Query("SELECT COUNT(a) > 0 FROM admin a WHERE a.AdminEmail = ?1")
    boolean existsByEmail(String email);

    @Query("SELECT a FROM admin a WHERE a.AdminEmail = ?1")
    admin findByAdminEmail(String email);

    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM admins", nativeQuery = true)
    int findNextAdminId();
}
