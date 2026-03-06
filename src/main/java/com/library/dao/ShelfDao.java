package com.library.dao;

import com.library.domain.Shelf;
import java.util.List;
import java.util.Optional;

public interface ShelfDao {
    int insert(String shelfNumber, String location) throws Exception;
    Optional<Shelf> findById(int id) throws Exception;
    List<Shelf> findAll() throws Exception;
    boolean update(int id, String shelfNumber, String location) throws Exception;
    boolean deleteById(int id) throws Exception;
}