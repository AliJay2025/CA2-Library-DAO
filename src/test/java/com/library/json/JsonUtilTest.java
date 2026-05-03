package com.library.json;

import com.library.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * F22: Tests for JSON serialization and deserialization
 * Tests that objects survive the JSON round-trip
 */
class JsonUtilTest {

    private Member testMember;
    private List<Member> testMemberList;

    // Creates test data before each test
    @BeforeEach
    void setUp() {
        testMember = new Member(1, "Ali Abdi", "123 Main St, Dublin", "087-123-4567");

        testMemberList = new ArrayList<>();
        testMemberList.add(new Member(1, "Ali Abdi", "123 Main St, Dublin", "087-123-4567"));
        testMemberList.add(new Member(2, "Mary Johnson", "45 Oak Ave, Cork", "086-234-5678"));
        testMemberList.add(new Member(3, "Mohammed Ali", "78 High St, Galway", "085-345-6789"));
    }

    // ===== F9: SINGLE MEMBER JSON ROUND-TRIP =====

    /** Test: Member to JSON and back returns equal object (F9 round-trip) */
    @Test
    void memberToJson_andBack_returnsEqualMember() throws Exception {
        // Serialize: Member -> JSON string
        String json = JsonUtil.memberToJson(testMember);

        // Deserialize: JSON string -> Member
        Member reconstructed = JsonUtil.memberFromJson(json);

        // Verify all fields match
        assertEquals(testMember.getId(), reconstructed.getId());
        assertEquals(testMember.getName(), reconstructed.getName());
        assertEquals(testMember.getAddress(), reconstructed.getAddress());
        assertEquals(testMember.getPhone(), reconstructed.getPhone());
    }

    /** Test: Empty member (default constructor) round-trip works */
    @Test
    void memberToJson_emptyMember_returnsEqualMember() throws Exception {
        Member empty = new Member();
        empty.setId(5);
        empty.setName("Test");
        empty.setAddress("Address");
        empty.setPhone("123");

        String json = JsonUtil.memberToJson(empty);
        Member reconstructed = JsonUtil.memberFromJson(json);

        assertEquals(empty.getId(), reconstructed.getId());
        assertEquals(empty.getName(), reconstructed.getName());
    }

    /** Test: Member with image data round-trip works (F17) */
    @Test
    void memberToJson_memberWithImageData_roundTripPreservesData() throws Exception {
        byte[] imageData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        testMember.setProfileImage(imageData);
        testMember.setFileName("test.png");
        testMember.setContentType("image/png");
        testMember.setFileSize(imageData.length);

        String json = JsonUtil.memberToJson(testMember);
        Member reconstructed = JsonUtil.memberFromJson(json);

        assertArrayEquals(imageData, reconstructed.getProfileImage());
        assertEquals("test.png", reconstructed.getFileName());
        assertEquals("image/png", reconstructed.getContentType());
        assertEquals(imageData.length, reconstructed.getFileSize());
    }

    // ===== F9: LIST JSON ROUND-TRIP =====

    /** Test: List of Members to JSON and back preserves all elements (F9) */
    @Test
    void memberListToJson_andBack_returnsEqualList() throws Exception {
        // Serialize: List<Member> -> JSON string
        String json = JsonUtil.memberListToJson(testMemberList);

        // Deserialize: JSON string -> List<Member>
        List<Member> reconstructed = JsonUtil.memberListFromJson(json);

        // Verify size and contents
        assertEquals(testMemberList.size(), reconstructed.size());

        for (int i = 0; i < testMemberList.size(); i++) {
            assertEquals(testMemberList.get(i).getId(), reconstructed.get(i).getId());
            assertEquals(testMemberList.get(i).getName(), reconstructed.get(i).getName());
            assertEquals(testMemberList.get(i).getAddress(), reconstructed.get(i).getAddress());
            assertEquals(testMemberList.get(i).getPhone(), reconstructed.get(i).getPhone());
        }
    }

    /** Test: Empty list JSON round-trip */
    @Test
    void memberListToJson_emptyList_returnsEmptyList() throws Exception {
        List<Member> emptyList = new ArrayList<>();

        String json = JsonUtil.memberListToJson(emptyList);
        List<Member> reconstructed = JsonUtil.memberListFromJson(json);

        assertNotNull(reconstructed);
        assertTrue(reconstructed.isEmpty());
    }

    /** Test: List with one member round-trip */
    @Test
    void memberListToJson_singleMemberList_returnsListWithOneMember() throws Exception {
        List<Member> singleList = new ArrayList<>();
        singleList.add(testMember);

        String json = JsonUtil.memberListToJson(singleList);
        List<Member> reconstructed = JsonUtil.memberListFromJson(json);

        assertEquals(1, reconstructed.size());
        assertEquals(testMember.getId(), reconstructed.get(0).getId());
    }

    // ===== EDGE CASE TESTS =====

    /** Test: JSON null handling - null returns null object */
    @Test
    void memberFromJson_nullJson_returnsNull() throws Exception {
        Member result = JsonUtil.memberFromJson(null);
        assertNull(result);
    }

    /** Test: JSON empty string handling */
    @Test
    void memberFromJson_emptyString_returnsNull() throws Exception {
        Member result = JsonUtil.memberFromJson("");
        assertNull(result);
    }

    /** Test: Member with special characters in name */
    @Test
    void memberToJson_specialCharacters_preservesCharacters() throws Exception {
        Member special = new Member(1, "O'Brian", "St. Patrick's Street", "087-123-4567");

        String json = JsonUtil.memberToJson(special);
        Member reconstructed = JsonUtil.memberFromJson(json);

        assertEquals("O'Brian", reconstructed.getName());
        assertEquals("St. Patrick's Street", reconstructed.getAddress());
    }

    // F22 Extended: Binary file upload/retrieval round-trip test
    @Test
    void binaryFile_uploadAndRetrieve_roundTripMatchesBytes() throws Exception {
        // Create test binary data
        byte[] originalBytes = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

        // Encode to Base64 (simulate upload)
        String encoded = Base64.getEncoder().encodeToString(originalBytes);

        // Decode back to bytes (simulate retrieval)
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);

        // Assert they match
        assertArrayEquals(originalBytes, decodedBytes);
    }

}