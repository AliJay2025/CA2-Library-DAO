package com.library.dao;

import com.library.domain.Staff;
import java.util.List;
import java.util.Optional;

public interface StaffDao {
    int insert(String name, String role, String contact) throws Exception;
    Optional<Staff> findById(int id) throws Exception;
    List<Staff> findAll() throws Exception;
    boolean update(int id, String name, String role, String contact) throws Exception;
    boolean deleteById(int id) throws Exception;
}