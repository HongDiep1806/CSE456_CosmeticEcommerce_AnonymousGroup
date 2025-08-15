package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.blogs;

public interface blogrepository extends CrudRepository<blogs, Integer> {

    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM  blogs", nativeQuery = true)
    int findNextBlogId();

    @Query(value = "SELECT * FROM blogs WHERE id != :id ORDER BY id DESC LIMIT 3", nativeQuery = true)
    List<blogs> findTop3ByIdNot(int id);


    @Query("SELECT b FROM blogs b WHERE b.BlogStatus != 'hidden' ORDER BY b.BlogId DESC")
        Page<blogs> findAll(Pageable pageable);
}
