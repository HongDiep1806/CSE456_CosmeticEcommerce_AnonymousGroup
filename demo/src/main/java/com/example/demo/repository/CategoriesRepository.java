package com.example.demo.repository;

import java.util.*;

import com.example.demo.model.Categories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends CrudRepository<Categories, Integer> {
    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM categories", nativeQuery = true)
    int findNextCategoryId();

    @Query("SELECT c, SUM(p.ProductQuantity) FROM Categories c LEFT JOIN Products p ON c.CategoryName = p.ProductCategory GROUP BY c")
    List<Object[]> findCategoriesWithTotalQuantity();

    List<Categories> findAllByIsActiveTrue();
}
