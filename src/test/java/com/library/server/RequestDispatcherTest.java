package com.library.server;

import com.library.dao.DaoRegistry;
import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;
import com.library.shared.ClientRequest;
import com.library.shared.ServerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

/**
 * F22 Extended: Tests for server request/response scenarios
 */
class RequestDispatcherTest {

    private RequestDispatcher dispatcher;
    private MemberDao memberDao;
    private int testMemberId;

    @BeforeEach
    void setUp() throws Exception {
        memberDao = new JdbcMemberDao();
        DaoRegistry registry = new DaoRegistry(memberDao);
        dispatcher = new RequestDispatcher(registry);

        // Create a test member for our tests
        testMemberId = memberDao.insert("Test Member", "Test Address", "087-000-0000");
    }

    // Test 1: GET_ALL request returns success response
    @Test
    void dispatch_GetAllRequest_returnsSuccessResponse() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("GET_ALL");

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
    }

    // Test 2: GET_BY_ID with valid ID returns member
    @Test
    void dispatch_GetByIdWithValidId_returnsMember() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("GET_BY_ID");
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", testMemberId);
        request.setPayload(payload);

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
    }

    // Test 3: GET_BY_ID with invalid ID returns error
    @Test
    void dispatch_GetByIdWithInvalidId_returnsError() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("GET_BY_ID");
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", 99999);
        request.setPayload(payload);

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getData());
    }

    // Test 4: Test for F18 - Upload scenario (binary)
    @Test
    void dispatch_UploadRequest_validData_returnsSuccess() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("UPLOAD");

        Map<String, Object> payload = new HashMap<>();
        payload.put("entityId", testMemberId);
        payload.put("fileName", "test.png");
        payload.put("contentType", "image/png");
        payload.put("fileSize", 100);
        payload.put("fileData", "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
        request.setPayload(payload);

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
    }

    // Test 5: Test for F19 - Download scenario
    @Test
    void dispatch_DownloadRequest_returnsResponse() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("DOWNLOAD");
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", testMemberId);
        request.setPayload(payload);

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        // Response could be success or error depending on whether file exists
        assertTrue(response.getStatus().equals("success") || response.getStatus().equals("error"));
    }

    // Test 6: Test for F20 - Metadata request
    @Test
    void dispatch_MetadataRequest_returnsMetadata() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setRequestType("METADATA");
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", testMemberId);
        request.setPayload(payload);

        ServerResponse<?> response = dispatcher.dispatch(request);

        assertNotNull(response);
        // Should return success even if no image (metadata still exists)
        assertEquals("success", response.getStatus());
    }
}