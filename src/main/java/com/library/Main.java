package com.library;

import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;
import com.library.json.JsonUtil;
import com.library.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static MemberDao memberDao = new JdbcMemberDao();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Test database connection first
        System.out.println("Testing database connection.....");
        try {
            java.sql.Connection conn = com.library.db.DatabaseConnection.getConnection();
            System.out.println("Connected to database: " + conn.getCatalog());
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1: GetAllMembers(); break;
                case 2: GetMemberById(); break;
                case 3: InsertMember(); break;
                case 4: UpdateMember(); break;
                case 5: FilterMembers(); break;
                case 6: JsonConversion(); break;
                case 7: DeleteMember(); break;
                case 0:
                    System.out.println("\nExiting... Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("  LIBRARY MANAGEMENT SYSTEM - STAGE 1");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1: Get All Members .................");
        System.out.println("  2: Get Member by ID ................");
        System.out.println("  3: Insert New Member ...............");
        System.out.println("  4: Update Member ...................");
        System.out.println("  5: Filter with Predicate ...........");
        System.out.println("  6: JSON Conversion .................");
        System.out.println("  7: Delete Member ...................");
        System.out.println("  0: Exit ............................");
        System.out.println("------------------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ===== F3: GET ALL MEMBERS =====
    private static void GetAllMembers() {
        System.out.println("\n--- F3: GET ALL MEMBERS ---");
        try {
            List<Member> allMembers = memberDao.findAll();
            System.out.println("   Total members found: " + allMembers.size());
            for (Member m : allMembers) {
                System.out.println("   - " + m);
            }
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F4: GET MEMBER BY ID =====
    private static void GetMemberById() {
        System.out.println("\n--- F4: GET MEMBER BY ID ---");
        System.out.print("   Enter Member ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Optional<Member> member = memberDao.findById(id);

            if (member.isPresent()) {
                System.out.println("   Found: " + member.get());
            } else {
                System.out.println("   Member with ID " + id + " not found (Optional.empty)");
            }
        } catch (NumberFormatException e) {
            System.out.println("   Invalid ID format");
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F6: INSERT MEMBER =====
    private static void InsertMember() {
        System.out.println("\n--- F6: INSERT NEW MEMBER ---");
        try {
            System.out.print("   Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("   Enter Address: ");
            String address = scanner.nextLine();
            System.out.print("   Enter Phone: ");
            String phone = scanner.nextLine();

            Member newMember = new Member(name, address, phone);
            System.out.println("   Before insert - ID: 0");

            int newId = memberDao.insert(newMember.getName(), newMember.getAddress(), newMember.getPhone());
            System.out.println("   After insert - ID: " + newId + " (auto-generated)");

            Optional<Member> inserted = memberDao.findById(newId);
            if (inserted.isPresent()) {
                System.out.println("   Verified: " + inserted.get());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("   Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F7: UPDATE MEMBER =====
    private static void UpdateMember() {
        System.out.println("\n--- F7: UPDATE MEMBER ---");
        try {
            System.out.print("   Enter Member ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> existing = memberDao.findById(id);
            if (!existing.isPresent()) {
                System.out.println("   Member with ID " + id + " not found");
                return;
            }

            System.out.println("   Current: " + existing.get());
            System.out.print("   Enter new Name (or press Enter to skip): ");
            String name = scanner.nextLine();
            System.out.print("   Enter new Address (or press Enter to skip): ");
            String address = scanner.nextLine();
            System.out.print("   Enter new Phone (or press Enter to skip): ");
            String phone = scanner.nextLine();

            Member toUpdate = existing.get();
            if (!name.trim().isEmpty()) toUpdate.setName(name);
            if (!address.trim().isEmpty()) toUpdate.setAddress(address);
            if (!phone.trim().isEmpty()) toUpdate.setPhone(phone);

            Member result = memberDao.update(id, toUpdate);

            if (result != null) {
                System.out.println("   Update successful: " + result);
            } else {
                System.out.println("   Update failed");
            }

        } catch (NumberFormatException e) {
            System.out.println("   Invalid ID format");
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F8: FILTER WITH PREDICATE =====
    private static void FilterMembers() {
        System.out.println("\n--- F8: FILTER WITH PREDICATE ---");
        try {
            System.out.println("   Choose filter:");
            System.out.println("   1. Names starting with 'A'");
            System.out.println("   2. Names starting with 'M'");
            System.out.println("   3. Dublin address");
            System.out.println("   4. Phone starting with '087'");
            System.out.print("   Enter choice: ");

            String choice = scanner.nextLine();
            List<Member> filtered = null;
            String filterDescription = "";

            switch (choice) {
                case "1":
                    filtered = memberDao.findAll().stream()
                            .filter(m -> m.getName().startsWith("A"))
                            .collect(java.util.stream.Collectors.toList());
                    filterDescription = "Names starting with 'A'";
                    break;
                case "2":
                    filtered = memberDao.findAll().stream()
                            .filter(m -> m.getName().startsWith("M"))
                            .collect(java.util.stream.Collectors.toList());
                    filterDescription = "Names starting with 'M'";
                    break;
                case "3":
                    filtered = memberDao.findAll().stream()
                            .filter(m -> m.getAddress().contains("Dublin"))
                            .collect(java.util.stream.Collectors.toList());
                    filterDescription = "Dublin address";
                    break;
                case "4":
                    filtered = memberDao.findAll().stream()
                            .filter(m -> m.getPhone().startsWith("087"))
                            .collect(java.util.stream.Collectors.toList());
                    filterDescription = "Phone starting with 087";
                    break;
                default:
                    System.out.println("   Invalid choice");
                    return;
            }

            System.out.println("   Members with " + filterDescription + ": " + filtered.size());
            for (Member m : filtered) {
                System.out.println("      - " + m.getName() + " | " + m.getPhone());
            }

        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F9: JSON CONVERSION =====
    private static void JsonConversion() {
        System.out.println("\n--- F9: JSON CONVERSION ---");
        try {
            System.out.print("   Enter Member ID for JSON test (default 1): ");
            String input = scanner.nextLine();
            int id = input.isEmpty() ? 1 : Integer.parseInt(input);

            Optional<Member> memberForJson = memberDao.findById(id);
            if (!memberForJson.isPresent()) {
                System.out.println("   Member with ID " + id + " not found");
                return;
            }

            Member m = memberForJson.get();

            // Member to JSON
            String json = JsonUtil.memberToJson(m);
            System.out.println("   Member to JSON: " + json);

            // JSON back to Member
            Member fromJson = JsonUtil.memberFromJson(json);
            System.out.println("   JSON to Member: " + fromJson);

            // List to JSON
            List<Member> allMembers = memberDao.findAll();
            List<Member> fewMembers = allMembers.subList(0, Math.min(3, allMembers.size()));
            String listJson = JsonUtil.memberListToJson(fewMembers);
            System.out.println("   List to JSON: " + listJson);

            // JSON back to List
            List<Member> listFromJson = JsonUtil.memberListFromJson(listJson);
            System.out.println("   JSON to List: " + listFromJson.size() + " members");

            // Round-trip verification
            boolean valid = m.getId() == fromJson.getId() &&
                    m.getName().equals(fromJson.getName()) &&
                    m.getAddress().equals(fromJson.getAddress()) &&
                    m.getPhone().equals(fromJson.getPhone());
            System.out.println("   Round-trip valid: " + (valid ? "YES" : "NO"));

        } catch (NumberFormatException e) {
            System.out.println("   Invalid ID format");
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }

    // ===== F5: DELETE MEMBER =====
    private static void DeleteMember() {
        System.out.println("\n--- F5: DELETE MEMBER ---");
        try {
            System.out.print("   Enter Member ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> existing = memberDao.findById(id);
            if (!existing.isPresent()) {
                System.out.println("   Member with ID " + id + " not found");
                return;
            }

            System.out.println("   Found: " + existing.get());
            System.out.print("   Are you sure you want to delete? (y/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                boolean deleted = memberDao.deleteById(id);
                System.out.println("   Delete result: " + (deleted ? "SUCCESS" : "FAILED"));

                Optional<Member> checkDeleted = memberDao.findById(id);
                if (!checkDeleted.isPresent()) {
                    System.out.println("   Verification: Member successfully deleted");
                } else {
                    System.out.println("   Verification: Member still exists");
                }
            } else {
                System.out.println("   Delete cancelled");
            }

        } catch (NumberFormatException e) {
            System.out.println("   Invalid ID format");
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
    }
}