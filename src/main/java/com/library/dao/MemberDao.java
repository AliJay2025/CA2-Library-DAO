package com.library.dao;

import com.library.domain.Member;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Member entity
 */
public interface MemberDao
{
    // F3: Get all members
    List<Member> findAll() throws Exception;

    // F4: Find member by ID (returns Optional)
    Optional<Member> findById(int id) throws Exception;

    // F6: Insert new member (returns auto-generated ID)
    int insert(String name, String address, String phone) throws Exception;

    // F7: Update member (returns updated Member)
    Member update(int id, Member member) throws Exception;

    // F5: Delete member by ID
    boolean deleteById(int id) throws Exception;

    // F17: Insert member with image data
    int insertMemberWithImage(Member member) throws Exception;

    // F20: Get metadata only (no BLOB) for a member
    Optional<Member> findMetadataById(int id) throws Exception;

    // F20: Get all members metadata only (no BLOB)
    List<Member> findAllMetadataOnly() throws Exception;
}