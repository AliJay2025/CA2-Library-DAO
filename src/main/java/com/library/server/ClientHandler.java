package com.library.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dao.DaoRegistry;
import com.library.shared.ClientRequest;
import com.library.shared.ServerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private final Socket _socket;
    private final ObjectMapper _mapper;
    private final RequestDispatcher _dispatcher;

    public ClientHandler(Socket socket, DaoRegistry registry)
    {
        if (socket == null)
            throw new IllegalArgumentException("socket is required");
        if (registry == null)
            throw new IllegalArgumentException("registry is required");

        _socket = socket;
        _mapper = new ObjectMapper();
        _dispatcher = new RequestDispatcher(registry);
    }

    @Override
    public void run()
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
             PrintWriter out = new PrintWriter(_socket.getOutputStream(), true))
        {
            String line;
            while ((line = in.readLine()) != null)
            {
                System.out.println("Received: " + line);
                String response = handle(line);
                out.println(response);
                System.out.println("Sent: " + response);
            }
        }
        catch (IOException e)
        {
            System.out.println("Client disconnected: " + e.getMessage());
        }
        finally
        {
            try { _socket.close(); } catch (IOException ignored) {}
        }
    }

    private String handle(String rawJson)
    {
        try
        {
            ClientRequest req = _mapper.readValue(rawJson, ClientRequest.class);
            ServerResponse<?> response = _dispatcher.dispatch(req);
            return _mapper.writeValueAsString(response);
        }
        catch (Exception e)
        {
            return toErrorJson("malformed request: " + e.getMessage());
        }
    }

    private String toErrorJson(String message)
    {
        String safeMessage = message == null ? "unknown error" : message.replace("\"", "\\\"");
        return "{\"status\":\"error\",\"message\":\"" + safeMessage + "\",\"data\":null}";
    }
}