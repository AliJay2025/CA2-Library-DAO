package com.library.server;

import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;
import com.library.shared.ServerResponse;

import java.util.List;
import java.util.Optional;

/**
 * Routes a deserialized {@link ClientRequest} to the correct DAO-backed operation.
 * This keeps protocol decision logic separate from socket handling.
 */
public class RequestDispatcher
{
    // === Fields ===
    private final MemberDao _dao;

    // === Constructors ===
    /**
     * Creates a dispatcher using the supplied DAO.
     *
     * @param dao The DAO used to perform member operations.
     */
    public RequestDispatcher(MemberDao dao)
    {
        if (dao == null)
            throw new IllegalArgumentException("dao is required");

        _dao = dao;
    }

    // === Methods ===
    /**
     * Dispatches a client request to the correct handler based on request type.
     *
     * @param req The client request.
     * @return A server response describing the outcome.
     */
    public ServerResponse<?> dispatch(ClientRequest req)
    {
        if (req == null)
            return ServerResponse.error("request is null");

        String typeText = req.getRequestType();

        if (typeText == null || typeText.isBlank())
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

    /**
     * Handles a request to retrieve all members.
     *
     * @return A response containing all members.
     * @throws Exception If the DAO operation fails.
     */
    private ServerResponse<List<Member>> handleGetAll() throws Exception
    {
        List<Member> members = _dao.findAll();
        return ServerResponse.success("Found " + members.size() + " members", members);
    }

    /**
     * Handles a request to retrieve a member by id.
     *
     * @param req The client request.
     * @return A response containing the matching member, if found.
     * @throws Exception If the DAO operation fails.
     */
    private ServerResponse<?> handleGetById(ClientRequest req) throws Exception
    {
        int id = req.getInt("id");

        if (id <= 0)
            return ServerResponse.error("valid id is required");

        Optional<Member> member = _dao.findById(id);

        if (member.isEmpty())
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member found", member.get());
    }

    /**
     * Handles a request to insert a new member.
     *
     * @param req The client request.
     * @return A response containing the created member.
     * @throws Exception If the DAO operation fails.
     */
    private ServerResponse<?> handleInsert(ClientRequest req) throws Exception
    {
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (name == null || name.isBlank())
            return ServerResponse.error("name is required");

        if (address == null || address.isBlank())
            return ServerResponse.error("address is required");

        if (phone == null || phone.isBlank())
            return ServerResponse.error("phone is required");

        int newId = _dao.insert(name, address, phone);
        Optional<Member> created = _dao.findById(newId);

        if (created.isEmpty())
            return ServerResponse.error("insert succeeded but member not found");

        return ServerResponse.success("Member inserted with ID " + newId, created.get());
    }

    /**
     * Handles a request to update an existing member.
     *
     * @param req The client request.
     * @return A response containing the updated member.
     * @throws Exception If the DAO operation fails.
     */
    private ServerResponse<?> handleUpdate(ClientRequest req) throws Exception
    {
        int id = req.getInt("id");
        String name = req.getString("name");
        String address = req.getString("address");
        String phone = req.getString("phone");

        if (id <= 0)
            return ServerResponse.error("valid id is required");

        if (name == null || name.isBlank())
            return ServerResponse.error("name is required");

        if (address == null || address.isBlank())
            return ServerResponse.error("address is required");

        if (phone == null || phone.isBlank())
            return ServerResponse.error("phone is required");

        Optional<Member> existing = _dao.findById(id);

        if (existing.isEmpty())
            return ServerResponse.error("no member with id=" + id);

        Member toUpdate = new Member(name, address, phone);
        Member result = _dao.update(id, toUpdate);

        if (result == null)
            return ServerResponse.error("update failed for id=" + id);

        return ServerResponse.success("Member updated", result);
    }

    /**
     * Handles a request to delete a member by id.
     *
     * @param req The client request.
     * @return A response indicating whether deletion succeeded.
     * @throws Exception If the DAO operation fails.
     */
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