package com.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * F22: Unit tests for Member domain class
 * Tests constructor validation, getters, and setters
 */
class MemberTest {

    private Member testMember;

    // Runs before each test - creates a fresh Member object
    @BeforeEach
    void setUp() {
        testMember = new Member(1, "Ali Abdi", "123 Main St, Dublin", "087-123-4567");
    }

    // ===== CONSTRUCTOR TESTS =====

    /** Test: Valid input creates member correctly */
    @Test
    void constructor_validInput_createsMemberWithCorrectValues() {
        assertEquals(1, testMember.getId());
        assertEquals("Ali Abdi", testMember.getName());
        assertEquals("123 Main St, Dublin", testMember.getAddress());
        assertEquals("087-123-4567", testMember.getPhone());
        assertFalse(testMember.hasImage());
    }

    /** Test: Null name throws exception (validation) */
    @Test
    void constructor_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(2, null, "456 Oak St", "087-999-8888"));
    }

    /** Test: Blank name throws exception (validation) */
    @Test
    void constructor_blankName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(2, "   ", "456 Oak St", "087-999-8888"));
    }

    /** Test: Null address throws exception */
    @Test
    void constructor_nullAddress_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(2, "John Doe", null, "087-999-8888"));
    }

    /** Test: Null phone throws exception */
    @Test
    void constructor_nullPhone_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(2, "John Doe", "456 Oak St", null));
    }

    /** Test: Negative ID throws exception */
    @Test
    void constructor_negativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(-5, "John Doe", "456 Oak St", "087-999-8888"));
    }

    // ===== SETTER TESTS =====

    /** Test: setName updates value correctly */
    @Test
    void setName_validName_updatesName() {
        testMember.setName("Updated Name");
        assertEquals("Updated Name", testMember.getName());
    }

    /** Test: setName with null throws exception */
    @Test
    void setName_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> testMember.setName(null));
    }

    /** Test: setAddress updates value correctly */
    @Test
    void setAddress_validAddress_updatesAddress() {
        testMember.setAddress("789 New St");
        assertEquals("789 New St", testMember.getAddress());
    }

    /** Test: setPhone updates value correctly */
    @Test
    void setPhone_validPhone_updatesPhone() {
        testMember.setPhone("087-111-2222");
        assertEquals("087-111-2222", testMember.getPhone());
    }

    // ===== IMAGE TESTS (F17) =====

    /** Test: hasImage returns false when no image set */
    @Test
    void hasImage_noImage_returnsFalse() {
        assertFalse(testMember.hasImage());
    }

    /** Test: hasImage returns true after image is set */
    @Test
    void hasImage_withImage_returnsTrue() {
        byte[] dummyImage = {1, 2, 3, 4, 5};
        testMember.setProfileImage(dummyImage);
        assertTrue(testMember.hasImage());
    }

    /** Test: getFileName returns correct value */
    @Test
    void getFileName_returnsFileName() {
        testMember.setFileName("profile.png");
        assertEquals("profile.png", testMember.getFileName());
    }

    /** Test: getFileSize returns correct value */
    @Test
    void getFileSize_returnsFileSize() {
        testMember.setFileSize(1024);
        assertEquals(1024, testMember.getFileSize());
    }
}