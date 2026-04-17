package com.library.dao;

import com.library.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberDao {
    // Existing methods
    int insert(String name, String address, String phone) throws Exception;
    Optional<Member> findById(int id) throws Exception;
    List<Member> findAll() throws Exception;
    boolean update(int id, Member member) throws Exception;
    boolean deleteById(int id) throws Exception;

    // F17: New method for inserting member with image
    int insertMemberWithImage(Member member) throws Exception;

    // F20: Metadata-only queries (no BLOB)
    Optional<Member> findMetadataById(int id) throws Exception;
    List<Member> findAllMetadataOnly() throws Exception;
}