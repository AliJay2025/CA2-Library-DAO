package com.library.server;

import com.library.dao.DaoRegistry;
import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryServer
{
    private final int _port;
    private final DaoRegistry _registry;
    private final ExecutorService _pool;

    public LibraryServer(int port, DaoRegistry registry)
    {
        if (port < 1_024 || port > 65_535)
            throw new IllegalArgumentException("port must be 1024-65535");
        if (registry == null)
            throw new IllegalArgumentException("registry is required");

        _port = port;
        _registry = registry;
        _pool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException
    {
        System.out.println("  LIBRARY SERVER - STAGE 2");
        System.out.println("----------------------------------------");
        System.out.println("Server listening on port " + _port);

        try (ServerSocket serverSocket = new ServerSocket(_port))
        {
            System.out.println("Server is running. Waiting for clients...\n");

            while (!Thread.currentThread().isInterrupted())
            {
                Socket client = serverSocket.accept();
                System.out.println("New client connected: " + client.getInetAddress());
                _pool.submit(new ClientHandler(client, _registry));
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        MemberDao memberDao = new JdbcMemberDao();
        DaoRegistry registry = new DaoRegistry(memberDao);
        new LibraryServer(8080, registry).start();
    }
}