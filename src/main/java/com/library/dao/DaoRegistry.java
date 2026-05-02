package com.library.dao;

public class DaoRegistry {

    // === Fields ===
    private final MemberDao _memberDao;

    // === Constructors ===
    public DaoRegistry(MemberDao memberDao) {
        if (memberDao == null)
            throw new IllegalArgumentException("memberDao is required");
        _memberDao = memberDao;
    }

    // === Public API ===
    public MemberDao members() { return _memberDao; }
}