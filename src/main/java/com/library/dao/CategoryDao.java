package com.library.dao;

import com.library.domain.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    int insert(String name) throws Exception;
    Optional<Category> findById(int id) throws Exception;
    List<Category> findAll() throws Exception;
    boolean update(int id, String name) throws Exception;
    boolean deleteById(int id) throws Exception;
}