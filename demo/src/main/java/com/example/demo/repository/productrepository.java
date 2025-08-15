package com.example.demo.repository;

import com.example.demo.model.products;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface productrepository extends CrudRepository<products, Integer> {
        @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM product", nativeQuery = true)
        int findNextProductId();

        @Query("SELECT p FROM products p WHERE p.ProductCategory = ?1")
        List<products> findProductsByCategory(String ProductCategory);

        @Query("SELECT p FROM products p WHERE p.ProductCategory = ?1 AND p.ProductId != ?2")
        List<products> findProductsByCategoryExcludingId(String productCategory, int productId);

        @Query("SELECT p FROM products p WHERE p.ProductCategory = (SELECT c.CategoryName FROM categories c WHERE c.CategoryId = ?1) ORDER BY p.ProductId ASC")
        List<products> findProductsByCategoryId(int categoryId, Pageable pageable);

        @Query("SELECT p FROM products p WHERE p.ProductStatus != 'block' ORDER BY p.ProductId DESC")
        List<products> findTop5Products(Pageable pageable);

        @Query("SELECT p FROM products p JOIN orderdetails od ON p.ProductId = od.ProductId " +
                        "GROUP BY p ORDER BY SUM(od.ProductQuantity) DESC")
        List<products> findBestProducts(Pageable pageable);

        @Query("SELECT p FROM products p WHERE p.ProductStatus != 'block' AND " +
                        "(p.ProductCategory = :category OR :category IS NULL) " +
                        "ORDER BY p.ProductId DESC")
        Page<products> findProductsByCategory(String category, Pageable pageable);

        @Query("SELECT p FROM products p WHERE p.ProductStatus != 'block' ORDER BY p.ProductId DESC")
        Page<products> findAllProducts(Pageable pageable);

        @Query("SELECT p FROM products p WHERE p.ProductCategory = :categoryName AND p.ProductPrice BETWEEN :minPrice AND :maxPrice")
        Page<products> findProductsByCategoryAndPriceRange(
                        @Param("categoryName") String categoryName,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        Pageable pageable);

        @Query("SELECT p FROM products p WHERE p.ProductPrice BETWEEN :minPrice AND :maxPrice")
        Page<products> findProductsByPriceRange(
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        Pageable pageable);
}
