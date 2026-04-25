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

public class RequestDispatcher {
    private final DaoRegistry _registry;

    public RequestDispatcher(DaoRegistry registry) {
        if (registry == null)
            throw new IllegalArgumentException("registry is required");
        _registry = registry;
    }

    public ServerResponse<?> dispatch(ClientRequest req) {
        if (req == null)
            return ServerResponse.error("request is null");

        String typeText = req.getRequestType();

        if (typeText == null || typeText.trim().isEmpty())
            return ServerResponse.error("requestType is required");

        try {
            RequestType type = RequestType.valueOf(typeText.trim().toUpperCase());

            switch (type) {
                case GET_ALL:
                    return handleGetAll();
                case GET_BY_ID:
                    return handleGetById(req);
                case INSERT:
                    return handleInsert(req);
                case UPDATE:
                    return handleUpdate(req);
                case DELETE:
                    return handleDelete(req);
                case UPLOAD:
                    return handleUpload(req);  // F18
                case DISCONNECT:
                    return ServerResponse.success("Goodbye", null);
                default:
                    return ServerResponse.error("unknown request type: " + typeText);
            }
        } catch (IllegalArgumentException e) {
            return ServerResponse.error("unknown request type: " + typeText);
        } catch (Exception e) {
            return ServerResponse.error("server error: " + e.getMessage());
        }
    }

    private ServerResponse<List<Member>> handleGetAll() throws Exception {
        MemberDao dao = _registry.members();
        List<Member> members = dao.findAll();
        return ServerResponse.success("Found " + members.size() + " members", members);
    }

    private ServerResponse<?> handleGetById(ClientRequest req) throws Exception {
        MemberDao dao = _registry.members();
        int id = req.getInt("id");
        if (id <= 0)
            return ServerResponse.error("valid id is required");

        Optional<Member> member = dao.findById(id);
        if (!member.isPresent())
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member found", member.get());
    }

    private ServerResponse<?> handleInsert(ClientRequest req) throws Exception {
        MemberDao dao = _registry.members();
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (name == null || name.trim().isEmpty())
            return ServerResponse.error("name is required");
        if (address == null || address.trim().isEmpty())
            return ServerResponse.error("address is required");
        if (phone == null || phone.trim().isEmpty())
            return ServerResponse.error("phone is required");

        int newId = dao.insert(name, address, phone);
        Optional<Member> created = dao.findById(newId);
        return ServerResponse.success("Member inserted with ID " + newId, created.get());
    }

    private ServerResponse<?> handleUpdate(ClientRequest req) throws Exception {
        MemberDao dao = _registry.members();
        int id = req.getInt("id");
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (id <= 0)
            return ServerResponse.error("valid id is required");
        if (name == null || name.trim().isEmpty())
            return ServerResponse.error("name is required");

        Optional<Member> existing = dao.findById(id);
        if (!existing.isPresent())
            return ServerResponse.error("no member with id=" + id);

        Member toUpdate = new Member(name, address, phone);
        Member result = dao.update(id, toUpdate);

        if (result == null)
            return ServerResponse.error("update failed for id=" + id);

        return ServerResponse.success("Member updated", result);
    }

    private ServerResponse<Void> handleDelete(ClientRequest req) throws Exception {
        MemberDao dao = _registry.members();
        int id = req.getInt("id");
        if (id <= 0)
            return ServerResponse.error("valid id is required");

        boolean deleted = dao.deleteById(id);
        if (!deleted)
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member deleted successfully", null);
    }

    /**
     * F18: Handles binary file upload requests.
     * Follows t16_json Section 7 and Section 8 patterns.
     * <p>
     * Steps:
     * 1. Extract metadata and Base64 data from request
     * 2. Decode Base64 back to byte[]
     * 3. Store in database using setBytes()
     */
    private ServerResponse<?> handleUpload(ClientRequest req) throws Exception {
        MemberDao dao = _registry.members();

        // Get the payload
        Object payloadObj = req.getPayload();
        if (payloadObj == null) {
            return ServerResponse.error("Upload payload is required");
        }

        // Convert payload to Map (following t15_networking pattern)
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadObj;

        // Extract metadata
        int entityId = ((Number) payload.get("entityId")).intValue();
        String fileName = (String) payload.get("fileName");
        String contentType = (String) payload.get("contentType");
        int fileSize = ((Number) payload.get("fileSize")).intValue();
        String fileDataBase64 = (String) payload.get("fileData");

        // Validate required fields
        if (fileName == null || fileName.trim().isEmpty()) {
            return ServerResponse.error("fileName is required");
        }
        if (fileDataBase64 == null || fileDataBase64.isEmpty()) {
            return ServerResponse.error("fileData is required");
        }

        // Decode Base64 to bytes (t16_json Section 7)
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

        // FIXED: update returns Member, not boolean
        Member result = dao.update(entityId, toUpdate);

        if (result == null) {
            return ServerResponse.error("Upload failed - could not update member");
        }

        // Return success response (without the image data)
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", result.getId());
        resultMap.put("name", result.getName());
        resultMap.put("fileName", result.getFileName());
        resultMap.put("fileSize", result.getFileSize());

        return ServerResponse.success("File uploaded successfully for member ID " + entityId, resultMap);
    }
}