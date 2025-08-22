package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.Blogs;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends CrudRepository<Blogs, Integer> {

    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM  blogs", nativeQuery = true)
    int findNextBlogId();

    @Query(value = "SELECT * FROM blogs WHERE id != :id ORDER BY id DESC LIMIT 3", nativeQuery = true)
    List<Blogs> findTop3ByIdNot(int id);


    @Query("SELECT b FROM Blogs b WHERE b.BlogStatus != 'hidden' ORDER BY b.BlogId DESC")
        Page<Blogs> findAll(Pageable pageable);
}
