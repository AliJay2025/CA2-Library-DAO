package com.library.server;

import com.library.dao.DaoRegistry;
import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;
import com.library.shared.ServerResponse;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Routes client requests to the correct handler methods
 */
public class RequestDispatcher
{
    private final DaoRegistry _registry;

    public RequestDispatcher(DaoRegistry registry)
    {
        _registry = registry;
    }

    /**
     * Main dispatch method - reads request type and calls the right handler
     */
    public ServerResponse<?> dispatch(ClientRequest req)
    {
        if (req == null) return ServerResponse.error("request is null");

        String typeText = req.getRequestType();
        if (typeText == null || typeText.trim().isEmpty())
            return ServerResponse.error("requestType is required");

        try
        {
            RequestType type = RequestType.valueOf(typeText.trim().toUpperCase());

            switch (type)
            {
                case GET_ALL:    return handleGetAll();      // F3
                case GET_BY_ID:  return handleGetById(req); // F4
                case INSERT:     return handleInsert(req);   // F6
                case UPDATE:     return handleUpdate(req);   // F7
                case DELETE:     return handleDelete(req);   // F5
                case UPLOAD:     return handleUpload(req);   // F18
                case DOWNLOAD:   return handleDownload(req); // F19
                case METADATA:   return handleMetadata(req); // F20
                case DISCONNECT: return ServerResponse.success("Goodbye", null); // F21
                default: return ServerResponse.error("unknown request type");
            }
        }
        catch (Exception e) {
            return ServerResponse.error("server error: " + e.getMessage());
        }
    }

    // ==================== F3: GET ALL MEMBERS ====================
    /** F3: Get all members from database and return as list */
    private ServerResponse<List<Member>> handleGetAll() throws Exception {
        List<Member> members = _registry.members().findAll();
        return ServerResponse.success("Found " + members.size() + " members", members);
    }

    // ==================== F4: GET MEMBER BY ID ====================
    /** F4: Find a single member by their ID number */
    private ServerResponse<?> handleGetById(ClientRequest req) throws Exception {
        int id = req.getInt("id");
        if (id <= 0) return ServerResponse.error("valid id required");

        Optional<Member> member = _registry.members().findById(id);
        if (!member.isPresent()) return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member found", member.get());
    }

    // ==================== F6: INSERT NEW MEMBER ====================
    /** F6: Add a new member to the database */
    private ServerResponse<?> handleInsert(ClientRequest req) throws Exception {
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (name == null || name.trim().isEmpty()) return ServerResponse.error("name required");
        if (address == null || address.trim().isEmpty()) return ServerResponse.error("address required");
        if (phone == null || phone.trim().isEmpty()) return ServerResponse.error("phone required");

        int newId = _registry.members().insert(name, address, phone);
        Optional<Member> created = _registry.members().findById(newId);
        return ServerResponse.success("Member inserted with ID " + newId, created.get());
    }

    // ==================== F7: UPDATE MEMBER ====================
    /** F7: Update an existing member's information */
    private ServerResponse<?> handleUpdate(ClientRequest req) throws Exception {
        int id = req.getInt("id");
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (id <= 0) return ServerResponse.error("valid id required");
        if (name == null || name.trim().isEmpty()) return ServerResponse.error("name required");

        Optional<Member> existing = _registry.members().findById(id);
        if (!existing.isPresent()) return ServerResponse.error("no member with id=" + id);

        Member toUpdate = new Member(name, address, phone);
        Member result = _registry.members().update(id, toUpdate);

        if (result == null) return ServerResponse.error("update failed");

        return ServerResponse.success("Member updated", result);
    }

    // ==================== F5: DELETE MEMBER ====================
    /** F5: Delete a member from the database by ID */
    private ServerResponse<Void> handleDelete(ClientRequest req) throws Exception {
        int id = req.getInt("id");
        if (id <= 0) return ServerResponse.error("valid id required");

        boolean deleted = _registry.members().deleteById(id);
        if (!deleted) return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member deleted successfully", null);
    }

    // ==================== F18: UPLOAD FILE ====================
    /** F18: Upload a file/image for a member - receives Base64, decodes, saves to database */
    private ServerResponse<?> handleUpload(ClientRequest req) throws Exception
    {
        MemberDao dao = _registry.members();

        // Get the payload from request
        Object payloadObj = req.getPayload();
        if (payloadObj == null) {
            return ServerResponse.error("Upload payload is required");
        }

        // Convert payload to Map
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadObj;

        // Extract file metadata
        int entityId = ((Number) payload.get("entityId")).intValue();
        String fileName = (String) payload.get("fileName");
        String contentType = (String) payload.get("contentType");
        int fileSize = ((Number) payload.get("fileSize")).intValue();
        String fileDataBase64 = (String) payload.get("fileData");

        // Validate inputs
        if (fileName == null || fileName.trim().isEmpty()) {
            return ServerResponse.error("fileName is required");
        }
        if (fileDataBase64 == null || fileDataBase64.isEmpty()) {
            return ServerResponse.error("fileData is required");
        }

        // Decode Base64 string back to bytes
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(fileDataBase64);
        } catch (IllegalArgumentException e) {
            return ServerResponse.error("Invalid Base64 data: " + e.getMessage());
        }

        // Check if member exists
        Optional<Member> existing = dao.findById(entityId);
        if (!existing.isPresent()) {
            return ServerResponse.error("Member with ID " + entityId + " not found");
        }

        // Update member with image data
        Member toUpdate = existing.get();
        toUpdate.setFileName(fileName);
        toUpdate.setContentType(contentType);
        toUpdate.setFileSize(fileSize);
        toUpdate.setProfileImage(imageBytes);

        // Save to database using setBytes()
        Member result = dao.update(entityId, toUpdate);

        if (result == null) {
            return ServerResponse.error("Upload failed - could not update member");
        }

        // Return success response
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", result.getId());
        resultMap.put("name", result.getName());
        resultMap.put("fileName", result.getFileName());
        resultMap.put("fileSize", result.getFileSize());

        return ServerResponse.success("File uploaded successfully for member ID " + entityId, resultMap);
    }

    // ==================== F19: DOWNLOAD FILE ====================
    /** F19: Download a file/image from a member - retrieves BLOB, encodes to Base64 */
    private ServerResponse<?> handleDownload(ClientRequest req) throws Exception
    {
        MemberDao dao = _registry.members();

        // Get the ID from request
        Object payloadObj = req.getPayload();
        if (payloadObj == null) {
            return ServerResponse.error("Download request requires an ID");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadObj;

        int id = ((Number) payload.get("id")).intValue();

        if (id <= 0) {
            return ServerResponse.error("Valid member ID is required");
        }

        // Fetch member including image from database
        Optional<Member> member = dao.findById(id);

        if (!member.isPresent()) {
            return ServerResponse.error("Member with ID " + id + " not found");
        }

        Member m = member.get();

        // Check if member has an image
        if (!m.hasImage()) {
            return ServerResponse.error("Member with ID " + id + " has no profile image");
        }

        // Build response with Base64-encoded image data
        Map<String, Object> fileData = new HashMap<>();
        fileData.put("id", m.getId());
        fileData.put("name", m.getName());
        fileData.put("fileName", m.getFileName());
        fileData.put("contentType", m.getContentType());
        fileData.put("fileSize", m.getFileSize());
        fileData.put("fileData", Base64.getEncoder().encodeToString(m.getProfileImage()));

        return ServerResponse.success("File retrieved successfully", fileData);
    }

    // ==================== F20: GET METADATA ONLY ====================
    /** F20: Get file metadata without downloading the actual file (no BLOB) */
    private ServerResponse<?> handleMetadata(ClientRequest req) throws Exception
    {
        MemberDao dao = _registry.members();

        // Get the payload
        Object payloadObj = req.getPayload();
        if (payloadObj == null) {
            return ServerResponse.error("Metadata request requires an ID");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadObj;

        // Check if asking for all members
        if (payload.containsKey("all") && "true".equals(payload.get("all"))) {
            List<Member> members = dao.findAllMetadataOnly();
            return ServerResponse.success("Retrieved metadata for " + members.size() + " members", members);
        }

        // Get metadata for specific ID (no BLOB data)
        int id = ((Number) payload.get("id")).intValue();

        if (id <= 0) {
            return ServerResponse.error("Valid member ID is required");
        }

        Optional<Member> member = dao.findMetadataById(id);

        if (!member.isPresent()) {
            return ServerResponse.error("Member with ID " + id + " not found");
        }

        Member m = member.get();

        // Return only metadata, no image bytes
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", m.getId());
        metadata.put("name", m.getName());
        metadata.put("address", m.getAddress());
        metadata.put("phone", m.getPhone());
        metadata.put("fileName", m.getFileName());
        metadata.put("contentType", m.getContentType());
        metadata.put("fileSize", m.getFileSize());
        metadata.put("hasImage", m.hasImage());

        return ServerResponse.success("Metadata retrieved for member " + id, metadata);
    }
}