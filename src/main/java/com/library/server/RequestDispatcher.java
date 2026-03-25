HafidG
        hafidg
        Online

        JayJay — 05/12/2024 13:23
        hye ximaaar
        where is the code
        💩
        HafidG — 05/12/2024 14:44
        Forwarded
        the hangman i did wasnt working fully but this version does. but you cant use this one exaclty as it is because it has stuff you havent doen yet. so just use it to try and understand the logic and use it to fix yours
        package Projects;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

message.txt
        5 KB
        HafidG — 08/03/2026 15:10
        https://github.com/abdihafidgahayr2-glitch/AESProject
        HafidG
        started a call that lasted a few seconds. — 15/03/2026 13:46
        HafidG — 15/03/2026 13:46
        Ali
        JayJay — Yesterday at 13:14
        file:///C:/Users/user/Downloads/2025-26-l8-s2-oop-gca2%20(3).html
        JayJay — 14:56
        package com.library.server;

import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;

message.txt
        6 KB
        ﻿
        JayJay
        jabra_1
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

        if (member.isEmpty())
            return ServerResponse.error("no member with id=" + id);

        return ServerResponse.success("Member found", member.get());
    }

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