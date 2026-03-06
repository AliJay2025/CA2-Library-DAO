package com.library.dao;

import com.library.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberDao {
    int insert(String name, String address, String phone) throws Exception;
    Optional<Member> findById(int id) throws Exception;
    List<Member> findAll() throws Exception;
    boolean update(int id, String name, String address, String phone) throws Exception;
    boolean deleteById(int id) throws Exception;
}