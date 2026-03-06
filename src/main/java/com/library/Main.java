package com.library;

import com.library.dao.MemberDao;
import com.library.dao.JdbcMemberDao;
import com.library.domain.Member;

import java.util.List;
import java.util.Optional;

public class Main {

    private static MemberDao memberDao = new JdbcMemberDao();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  LIBRARY MANAGEMENT SYSTEM - DEMO");
        System.out.println("=========================================\n");

        createMember();
        readMember();
        updateMember();
        deleteMember();
    }

    private static void createMember() {
        try {
            System.out.println("-- CREATE MEMBER --");
            int newId = memberDao.insert("ALi Jabriil", "123 Main St, Dundalk", "087-123-4567");
            System.out.println("   Inserted member with id: " + newId);

            Optional<Member> m = memberDao.findById(newId);
            m.ifPresent(mem -> System.out.println("   Verified: " + mem));
            System.out.println();
        } catch (Exception e) {
            System.out.println("   CREATE failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void readMember() {
        try {
            System.out.println("-- READ MEMBER --");

            Optional<Member> found = memberDao.findById(1);
            if (found.isPresent())
                System.out.println("   Found by id 1: " + found.get());
            else
                System.out.println("   No member found with id 1");

            List<Member> allMembers = memberDao.findAll();
            System.out.println("   All members (" + allMembers.size() + "):");
            for (Member m : allMembers.subList(0, Math.min(5, allMembers.size())))
                System.out.println("      " + m);
            System.out.println();
        } catch (Exception e) {
            System.out.println("   READ failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateMember() {
        try {
            System.out.println("-- UPDATE MEMBER --");

            int tempId = memberDao.insert("Temp User", "Temp Address", "087-000-0000");
            System.out.println("   Inserted temporary member with id: " + tempId);

            boolean updated = memberDao.update(tempId, "Updated Name", "Updated Address", "087-999-9999");
            System.out.println("   Updated member " + tempId + ": " + updated);

            Optional<Member> updatedMember = memberDao.findById(tempId);
            updatedMember.ifPresent(m -> System.out.println("   After update: " + m));

            memberDao.deleteById(tempId);
            System.out.println();
        } catch (Exception e) {
            System.out.println("   UPDATE failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteMember() {
        try {
            System.out.println("-- DELETE MEMBER --");

            int tempId = memberDao.insert("Delete Me", "Delete Address", "087-111-1111");
            System.out.println("   Inserted temporary member with id: " + tempId);

            boolean deleted = memberDao.deleteById(tempId);
            System.out.println("   Deleted member " + tempId + ": " + deleted);

            Optional<Member> deletedMember = memberDao.findById(tempId);
            if (!deletedMember.isPresent())  // FIXED: changed from isEmpty()
                System.out.println("   Confirmed: member no longer exists");
            System.out.println();
        } catch (Exception e) {
            System.out.println("   DELETE failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}