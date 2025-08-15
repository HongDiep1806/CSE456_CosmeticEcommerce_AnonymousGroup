package com.example.demo.repository;

import java.util.*;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.productotherimages;
import com.example.demo.model.products;

public interface productimagesrepository extends CrudRepository<productotherimages, Integer> {
    List<productotherimages> findByProduct(products product);
}
