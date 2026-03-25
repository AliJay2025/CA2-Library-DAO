package com.library.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;
import com.library.model.ServerResponse;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Starts the JSON protocol server and listens for incoming TCP client connections.
 * Each client is handed to a {@link ClientHandler} using an {@link ExecutorService}.
 *
 * F10: Uses ExecutorService for multithreading
 * F11: All responses use ServerResponse<T> wrapper
 */
public class LibraryServer
{
    // === Fields ===
    private final int _port;
    private final MemberDao _dao;
    private final ExecutorService _pool;

    // === Constructors ===
    /**
     * Creates a server on the given port using the supplied DAO.
     *
     * @param port The TCP port to listen on (1024-65535).
     * @param dao The DAO used by request handlers.
     */
    public LibraryServer(int port, MemberDao dao)
    {
        if (port < 1_024 || port > 65_535)
            throw new IllegalArgumentException("port must be 1024-65535");

        if (dao == null)
            throw new IllegalArgumentException("dao is required");

        _port = port;
        _dao = dao;
        _pool = Executors.newCachedThreadPool();  // F10: Thread pool for multiple clients
    }

    // === Methods ===
    /**
     * Starts the server accept loop.
     * Each accepted socket is processed by a pooled {@link ClientHandler}.
     *
     * @throws IOException If the server socket cannot be opened or used.
     */
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
                _pool.submit(new ClientHandler(client, _dao));  // Each client gets its own thread
            }
        }
    }

    /**
     * Entry point for launching the server.
     *
     * @param args Command-line arguments.
     * @throws Exception If startup fails.
     */
    public static void main(String[] args) throws Exception
    {
        MemberDao dao = new JdbcMemberDao();  // Uses interface, not implementation
        new LibraryServer(8080, dao).start();
    }
}