package com.library.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dao.MemberDao;
import com.library.jdbc.JdbcMemberDao;
import com.library.domain.Member;
import com.library.model.ServerResponse;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LibraryServer {

    // The port number where the server listens for connections (8080 is a common port for testing)
    private static final int PORT = 8080;

    // Jackson's ObjectMapper - converts Java objects to JSON and back
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // The DAO that talks to the database - uses interface, not implementation
    private static final MemberDao memberDao = new JdbcMemberDao();


    public static void main(String[] args) {
        System.out.println("  LIBRARY SERVER - STAGE 2");
        System.out.println("----------------------------------------");
        System.out.println("Server starting on port " + PORT + "...");

        // try-with-resources - automatically closes the ServerSocket when done
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Creates a thread pool that creates new threads as needed
            // This is the key to handling multiple clients (F10)
            ExecutorService threadPool = Executors.newCachedThreadPool();
            System.out.println("Server is running. Waiting for clients...\n");

            // Infinite loop - server runs forever, waiting for clients
            while (true) {
                // accept() BLOCKS until a client connects
                // When a client connects, we get a Socket object to communicate with them
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Give this client to a thread from the pool
                // Each client runs in its own thread - this is the multithreading part!
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * ClientHandler - handles communication with ONE client
     * Each client gets its own instance of this class, running in its own thread
     *
     * Implements Runnable so it can be run on a separate thread
     */
    static class ClientHandler implements Runnable {

        private Socket socket;           // The connection to this specific client
        private ObjectMapper mapper = new ObjectMapper();  // For JSON conversion

        /**
         * Constructor - stores the socket for this client
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * This method runs in a separate thread for each client
         * It reads requests from the client, processes them, and sends back responses
         */
        @Override
        public void run() {
            // try-with-resources - automatically closes streams when done
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                String requestJson;

                // Keep reading requests from this client until they disconnect
                while ((requestJson = reader.readLine()) != null) {
                    System.out.println("Received: " + requestJson);

                    // Convert JSON string to a Map (key-value pairs)
                    Map<String, Object> request = mapper.readValue(requestJson, Map.class);

                    // Extract the request type (e.g., "GET_ALL_MEMBERS", "INSERT_MEMBER")
                    String requestType = (String) request.get("requestType");

                    // Process the request and get a ServerResponse
                    ServerResponse<?> response = processRequest(requestType, request);

                    // Convert response to JSON and send back to client
                    String responseJson = mapper.writeValueAsString(response);
                    writer.println(responseJson);
                    System.out.println("Sent: " + responseJson);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) {}
            }
        }

        /**
         * Processes different types of requests from the client
         *
         * @param requestType - what operation to perform (GET, INSERT, UPDATE, DELETE)
         * @param request - the request data (contains ID, fields, etc.)
         * @return ServerResponse - wrapper with status, message, and data
         */
        private ServerResponse<?> processRequest(String requestType, Map<String, Object> request) {
            try {
                // Switch based on the request type
                switch (requestType) {

                    // F12: Get all members from database
                    case "GET_ALL_MEMBERS":
                        List<Member> members = memberDao.findAll();
                        return ServerResponse.success("Found " + members.size() + " members", members);

                    // F12: Get a single member by ID
                    case "GET_MEMBER_BY_ID":
                        int id = ((Number) request.get("id")).intValue();
                        java.util.Optional<Member> member = memberDao.findById(id);
                        if (member.isPresent()) {
                            return ServerResponse.success("Member found", member.get());
                        } else {
                            // Member not found - return error response (F16)
                            return ServerResponse.error("Member with ID " + id + " not found");
                        }

                        // F13: Insert a new member
                    case "INSERT_MEMBER":
                        // Extract data from request
                        Map<String, String> data = (Map<String, String>) request.get("data");
                        String name = data.get("name");
                        String address = data.get("address");
                        String phone = data.get("phone");

                        // Insert into database (returns auto-generated ID - F6)
                        int newId = memberDao.insert(name, address, phone);

                        // Fetch the newly inserted member
                        Member newMember = memberDao.findById(newId).orElse(null);
                        return ServerResponse.success("Member inserted with ID " + newId, newMember);

                    // F15: Update an existing member
                    case "UPDATE_MEMBER":
                        Map<String, Object> updateData = (Map<String, Object>) request.get("data");
                        int updateId = ((Number) updateData.get("id")).intValue();
                        String newName = (String) updateData.get("name");
                        String newAddress = (String) updateData.get("address");
                        String newPhone = (String) updateData.get("phone");

                        Member toUpdate = new Member(newName, newAddress, newPhone);
                        Member result = memberDao.update(updateId, toUpdate);

                        if (result != null) {
                            return ServerResponse.success("Member updated", result);
                        } else {
                            return ServerResponse.error("Update failed - member not found");
                        }

                        // F14: Delete a member by ID
                    case "DELETE_MEMBER":
                        int deleteId = ((Number) request.get("id")).intValue();
                        boolean deleted = memberDao.deleteById(deleteId);
                        if (deleted) {
                            return ServerResponse.success("Member deleted successfully", null);
                        } else {
                            return ServerResponse.error("Delete failed - member not found");
                        }

                        // Unknown request type - return error (F16)
                    default:
                        return ServerResponse.error("Unknown request type: " + requestType);
                }
            } catch (Exception e) {
                // Catch any exception and return a structured error response (F16)
                // Never propagate exceptions to the client
                e.printStackTrace();
                return ServerResponse.error("Server error: " + e.getMessage());
            }
        }
    }
}
