package com.example.demo.repository;

import java.util.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.categories;

public interface categoriesrepository extends CrudRepository<categories, Integer> {
    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM categories", nativeQuery = true)
    int findNextCategoryId();

    @Query("SELECT c, SUM(p.ProductQuantity) FROM categories c LEFT JOIN products p ON c.CategoryName = p.ProductCategory GROUP BY c")
    List<Object[]> findCategoriesWithTotalQuantity();

    List<categories> findAllByIsActiveTrue();
}
