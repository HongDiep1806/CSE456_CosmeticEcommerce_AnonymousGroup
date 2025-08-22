package com.example.demo.repository;

import com.example.demo.model.Products;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Products, Integer> {
        @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM product", nativeQuery = true)
        int findNextProductId();

        @Query("SELECT p FROM Products p WHERE p.ProductCategory = ?1")
        List<Products> findProductsByCategory(String ProductCategory);

        @Query("SELECT p FROM Products p WHERE p.ProductCategory = ?1 AND p.ProductId != ?2")
        List<Products> findProductsByCategoryExcludingId(String productCategory, int productId);

        @Query("SELECT p FROM Products p WHERE p.ProductCategory = (SELECT c.CategoryName FROM Categories c WHERE c.CategoryId = ?1) ORDER BY p.ProductId ASC")
        List<Products> findProductsByCategoryId(int categoryId, Pageable pageable);

        @Query("SELECT p FROM Products p WHERE p.ProductStatus != 'block' ORDER BY p.ProductId DESC")
        List<Products> findTop5Products(Pageable pageable);

        @Query("SELECT p FROM Products p JOIN OrderDetails od ON p.ProductId = od.ProductId " +
                        "GROUP BY p ORDER BY SUM(od.ProductQuantity) DESC")
        List<Products> findBestProducts(Pageable pageable);

        @Query("SELECT p FROM Products p WHERE p.ProductStatus != 'block' AND " +
                        "(p.ProductCategory = :category OR :category IS NULL) " +
                        "ORDER BY p.ProductId DESC")
        Page<Products> findProductsByCategory(String category, Pageable pageable);

        @Query("SELECT p FROM Products p WHERE p.ProductStatus != 'block' ORDER BY p.ProductId DESC")
        Page<Products> findAllProducts(Pageable pageable);

        @Query("SELECT p FROM Products p WHERE p.ProductCategory = :categoryName AND p.ProductPrice BETWEEN :minPrice AND :maxPrice")
        Page<Products> findProductsByCategoryAndPriceRange(
                        @Param("categoryName") String categoryName,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        Pageable pageable);

        @Query("SELECT p FROM Products p WHERE p.ProductPrice BETWEEN :minPrice AND :maxPrice")
        Page<Products> findProductsByPriceRange(
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        Pageable pageable);
}
