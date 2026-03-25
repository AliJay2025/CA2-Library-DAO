package com.library.server;

import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;
import com.library.shared.ServerResponse;

import java.util.List;
import java.util.Optional;

public class RequestDispatcher
{
    private final MemberDao _dao;

    public RequestDispatcher(MemberDao dao)
    {
        if (dao == null)
            throw new IllegalArgumentException("dao is required");
        _dao = dao;
    }

    public ServerResponse<?> dispatch(ClientRequest req)
    {
        if (req == null)
            return ServerResponse.error("request is null");

        String typeText = req.getRequestType();

        if (typeText == null || typeText.trim().isEmpty())
            return ServerResponse.error("requestType is required");

        try
        {
            RequestType type = RequestType.valueOf(typeText.trim().toUpperCase());

            switch (type)
            {
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
                case DISCONNECT:
                    return ServerResponse.success("Goodbye", null);
                default:
                    return ServerResponse.error("unknown request type: " + typeText);
            }
        }
        catch (IllegalArgumentException e)
        {
            return ServerResponse.error("unknown request type: " + typeText);
        }
        catch (Exception e)
        {
            return ServerResponse.error("server error: " + e.getMessage());
        }
    }

    private ServerResponse<List<Member>> handleGetAll() throws Exception
    {
        List<Member> members = _dao.findAll();
        return ServerResponse.success("Found " + members.size() + " members", members);
    }

    private ServerResponse<?> handleGetById(ClientRequest req) throws Exception
    {
        int id = req.getInt("id");
        if (id <= 0)
            return ServerResponse.error("valid id is required");

        Optional<Member> member = _dao.findById(id);
        // FIXED: Java 8 uses !isPresent() instead of isEmpty()
        if (!member.isPresent())
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member found", member.get());
    }

    private ServerResponse<?> handleInsert(ClientRequest req) throws Exception
    {
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (name == null || name.trim().isEmpty())
            return ServerResponse.error("name is required");

        if (address == null || address.trim().isEmpty())
            return ServerResponse.error("address is required");

        if (phone == null || phone.trim().isEmpty())
            return ServerResponse.error("phone is required");

        int newId = _dao.insert(name, address, phone);
        Optional<Member> created = _dao.findById(newId);

        // FIXED: Java 8 uses !isPresent() instead of isEmpty()
        if (!created.isPresent())
            return ServerResponse.error("insert succeeded but member not found");

        return ServerResponse.success("Member inserted with ID " + newId, created.get());
    }

    private ServerResponse<?> handleUpdate(ClientRequest req) throws Exception
    {
        int id = req.getInt("id");
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (id <= 0)
            return ServerResponse.error("valid id is required");

        if (name == null || name.trim().isEmpty())
            return ServerResponse.error("name is required");

        if (address == null || address.trim().isEmpty())
            return ServerResponse.error("address is required");

        if (phone == null || phone.trim().isEmpty())
            return ServerResponse.error("phone is required");

        Optional<Member> existing = _dao.findById(id);
        // FIXED: Java 8 uses !isPresent() instead of isEmpty()
        if (!existing.isPresent())
            return ServerResponse.error("no member with id=" + id);

        Member toUpdate = new Member(name, address, phone);
        Member result = _dao.update(id, toUpdate);

        if (result == null)
            return ServerResponse.error("update failed for id=" + id);

        return ServerResponse.success("Member updated", result);
    }

    private ServerResponse<Void> handleDelete(ClientRequest req) throws Exception
    {
        int id = req.getInt("id");
        if (id <= 0)
            return ServerResponse.error("valid id is required");

        boolean deleted = _dao.deleteById(id);
        if (!deleted)
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member deleted successfully", null);
    }
}