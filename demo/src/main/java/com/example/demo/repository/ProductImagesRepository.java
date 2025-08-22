package com.example.demo.repository;

import java.util.*;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.ProductOtherImages;
import com.example.demo.model.Products;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImagesRepository extends CrudRepository<ProductOtherImages, Integer> {
    List<ProductOtherImages> findByProduct(Products product);
}
