package com.library;

import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;
import com.library.json.JsonUtil;
import com.library.domain.Member;

import java.util.List;
import java.util.Optional;

public class Main {

    private static MemberDao memberDao = new JdbcMemberDao();

    public static void main(String[] args) {
        // Test database connection first
        System.out.println("Testing database connection.....");
        try {
            java.sql.Connection conn = com.library.db.DatabaseConnection.getConnection();
            System.out.println("Connected to database: " + conn.getCatalog());
            conn.close();
        } catch (Exception e) {
            System.out.println(" Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.println("  DAO Layer and Full CRUD - STAGE 1");
        System.out.println("------------------------------------------------\n");


        int testId = -1;

        try {
            // ===== F3: GET ALL MEMBERS =====
            System.out.println("--- F3: GET ALL MEMBERS ---");
            List<Member> allMembers = memberDao.findAll();
            System.out.println("   Total members found: " + allMembers.size());
            for (Member m : allMembers) {
                System.out.println("   - " + m);
            }
            System.out.println();

            // ===== F4: GET MEMBER BY ID =====
            System.out.println("--- F4: GET MEMBER BY ID ---");
            Optional<Member> member1 = memberDao.findById(1);
            if (member1.isPresent()) {
                System.out.println("   Found member with ID 1: " + member1.get());
            } else {
                System.out.println("   Member with ID 1 not found");
            }

            Optional<Member> nonExistent = memberDao.findById(999);
            System.out.println("   Get non-existent ID 999: " +
                    (!nonExistent.isPresent() ? "Optional.empty() ✓" : "ERROR"));
            System.out.println();

            // ===== F6: INSERT MEMBER =====
            System.out.println("--- F6: INSERT MEMBER ---");
            Member newMember = new Member("Mohamed Raja", "123 Test St, Dublin", "087-123-4567");
            System.out.println("   Before insert - ID: 0");

            testId = memberDao.insert(newMember.getName(), newMember.getAddress(), newMember.getPhone());
            System.out.println("   After insert - ID: " + testId + " (auto-generated) ✓");

            Optional<Member> inserted = memberDao.findById(testId);
            inserted.ifPresent(m -> System.out.println("   Verified: " + m));
            System.out.println();

            // ===== F7: UPDATE MEMBER =====
            System.out.println("--- F7: UPDATE MEMBER ---");
            Member updatedMember = new Member("Updated Name F7", "Updated Address", "087-999-8888");
            Member result = memberDao.update(testId, updatedMember);  // Now returns Member

            if (result != null) {
                System.out.println("   Update successful: " + result);
            } else {
                System.out.println("   Update failed");
            }

            // ===== F8: FILTER WITH PREDICATE =====
            System.out.println("--- F8: FILTER WITH PREDICATE ---");

            List<Member> startsWithA = memberDao.findAll().stream()
                    .filter(m -> m.getName().startsWith("A"))
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("   Members with names starting with 'A': " + startsWithA.size());

            List<Member> dublinMembers = memberDao.findAll().stream()
                    .filter(m -> m.getAddress().contains("Dublin"))
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("   Members in Dublin: " + dublinMembers.size());
            System.out.println();

            // ===== F9: JSON CONVERSION =====
            System.out.println("--- F9: JSON CONVERSION ---");

            Optional<Member> memberForJson = memberDao.findById(1);
            if (memberForJson.isPresent()) {
                Member m = memberForJson.get();

                String json = JsonUtil.memberToJson(m);
                System.out.println("   Member to JSON: " + json);

                Member fromJson = JsonUtil.memberFromJson(json);
                System.out.println("   JSON to Member: " + fromJson);

                List<Member> fewMembers = memberDao.findAll().subList(0, Math.min(3, allMembers.size()));
                String listJson = JsonUtil.memberListToJson(fewMembers);
                System.out.println("   List to JSON: " + listJson);

                List<Member> listFromJson = JsonUtil.memberListFromJson(listJson);
                System.out.println("   JSON to List: " + listFromJson.size() + " members");
            }
            System.out.println();

            // ===== F5: DELETE MEMBER =====
            System.out.println("--- F5: DELETE MEMBER ---");
            boolean deleted = memberDao.deleteById(testId);
            System.out.println("   Delete result: " + (deleted ? "SUCCESS ✓" : "FAILED"));

            Optional<Member> checkDeleted = memberDao.findById(testId);
            System.out.println("   Verification: " +
                    (!checkDeleted.isPresent() ? "Member successfully deleted ✓" : "ERROR"));
            System.out.println();


        } catch (Exception e) {
            System.err.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}