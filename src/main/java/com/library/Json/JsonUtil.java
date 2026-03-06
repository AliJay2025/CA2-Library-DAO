package com.library.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.domain.Member;
import com.library.domain.Book;
import com.library.domain.Category;
import com.library.domain.Shelf;
import com.library.domain.Staff;

import java.util.List;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ========== MEMBER JSON METHODS ==========
    public static String memberToJson(Member member) throws Exception {
        return MAPPER.writeValueAsString(member);
    }

    public static Member memberFromJson(String json) throws Exception {
        return MAPPER.readValue(json, Member.class);
    }

    public static String memberListToJson(List<Member> members) throws Exception {
        return MAPPER.writeValueAsString(members);
    }

    public static List<Member> memberListFromJson(String json) throws Exception {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Member.class));
    }

    // ========== BOOK JSON METHODS ==========
    public static String bookToJson(Book book) throws Exception {
        return MAPPER.writeValueAsString(book);
    }

    public static Book bookFromJson(String json) throws Exception {
        return MAPPER.readValue(json, Book.class);
    }

    public static String bookListToJson(List<Book> books) throws Exception {
        return MAPPER.writeValueAsString(books);
    }

    public static List<Book> bookListFromJson(String json) throws Exception {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Book.class));
    }

    // ========== CATEGORY JSON METHODS ==========
    public static String categoryToJson(Category category) throws Exception {
        return MAPPER.writeValueAsString(category);
    }

    public static Category categoryFromJson(String json) throws Exception {
        return MAPPER.readValue(json, Category.class);
    }

    public static String categoryListToJson(List<Category> categories) throws Exception {
        return MAPPER.writeValueAsString(categories);
    }

    public static List<Category> categoryListFromJson(String json) throws Exception {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Category.class));
    }

    // ========== SHELF JSON METHODS ==========
    public static String shelfToJson(Shelf shelf) throws Exception {
        return MAPPER.writeValueAsString(shelf);
    }

    public static Shelf shelfFromJson(String json) throws Exception {
        return MAPPER.readValue(json, Shelf.class);
    }

    public static String shelfListToJson(List<Shelf> shelves) throws Exception {
        return MAPPER.writeValueAsString(shelves);
    }

    public static List<Shelf> shelfListFromJson(String json) throws Exception {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Shelf.class));
    }

    // ========== STAFF JSON METHODS ==========
    public static String staffToJson(Staff staff) throws Exception {
        return MAPPER.writeValueAsString(staff);
    }

    public static Staff staffFromJson(String json) throws Exception {
        return MAPPER.readValue(json, Staff.class);
    }

    public static String staffListToJson(List<Staff> staffList) throws Exception {
        return MAPPER.writeValueAsString(staffList);
    }

    public static List<Staff> staffListFromJson(String json) throws Exception {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Staff.class));
    }

    // ========== VERIFICATION METHOD ==========
    public static boolean verifyMemberRoundTrip(Member original) throws Exception {
        String json = memberToJson(original);
        Member converted = memberFromJson(json);

        boolean isValid = original.getId() == converted.getId() &&
                original.getName().equals(converted.getName()) &&
                original.getAddress().equals(converted.getAddress()) &&
                original.getPhone().equals(converted.getPhone());

        System.out.println("Original: " + original);
        System.out.println("JSON: " + json);
        System.out.println("Converted: " + converted);
        System.out.println("Round-trip valid: " + isValid);

        return isValid;
    }
}