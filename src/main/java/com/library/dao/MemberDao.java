package com.library.dao;

import com.library.domain.Member;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface MemberDao {
    int insert(String name, String address, String phone) throws Exception;
    Optional<Member> findById(int id) throws Exception;
    List<Member> findAll() throws Exception;
    Member update(int id, Member member) throws Exception;
    boolean deleteById(int id) throws Exception;

    List<Member> findByFilter(Predicate<Member> filter) throws Exception;
}
