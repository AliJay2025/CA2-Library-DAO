package com.library.dao;

import com.library.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookDao {
    int insert(String title, String author, int categoryId, int shelfId, int availableCopies) throws Exception;
    Optional<Book> findById(int id) throws Exception;
    List<Book> findAll() throws Exception;
    boolean update(int id, String title, String author, int categoryId, int shelfId, int availableCopies) throws Exception;
    boolean deleteById(int id) throws Exception;
}