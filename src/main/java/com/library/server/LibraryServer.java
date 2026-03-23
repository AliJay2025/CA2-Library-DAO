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
    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MemberDao memberDao = new JdbcMemberDao();

    public static void main(String[] args) {
        System.out.println("  LIBRARY SERVER - STAGE 2");
        System.out.println("=========================================");
        System.out.println("Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            System.out.println(" Server is running. Waiting for clients...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("📱 New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println(" Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectMapper mapper = new ObjectMapper();

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                String requestJson;
                while ((requestJson = reader.readLine()) != null) {
                    System.out.println(" Received: " + requestJson);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> request = mapper.readValue(requestJson, Map.class);
                    String requestType = (String) request.get("requestType");

                    ServerResponse<?> response = processRequest(requestType, request);

                    String responseJson = mapper.writeValueAsString(response);
                    writer.println(responseJson);
                    System.out.println(" Sent: " + responseJson);
                }
            } catch (IOException e) {
                System.out.println(" Client disconnected: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) {}
            }
        }

        @SuppressWarnings("unchecked")
        private ServerResponse<?> processRequest(String requestType, Map<String, Object> request) {
            try {
                switch (requestType) {
                    case "GET_ALL_MEMBERS":
                        List<Member> members = memberDao.findAll();
                        return ServerResponse.success("Found " + members.size() + " members", members);

                    case "GET_MEMBER_BY_ID":
                        int id = ((Number) request.get("id")).intValue();
                        java.util.Optional<Member> member = memberDao.findById(id);
                        if (member.isPresent()) {
                            return ServerResponse.success("Member found", member.get());
                        } else {
                            return ServerResponse.error("Member with ID " + id + " not found");
                        }

                    case "INSERT_MEMBER":
                        Map<String, String> data = (Map<String, String>) request.get("data");
                        String name = data.get("name");
                        String address = data.get("address");
                        String phone = data.get("phone");

                        int newId = memberDao.insert(name, address, phone);
                        Member newMember = memberDao.findById(newId).orElse(null);
                        return ServerResponse.success("Member inserted with ID " + newId, newMember);

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

                    case "DELETE_MEMBER":
                        int deleteId = ((Number) request.get("id")).intValue();
                        boolean deleted = memberDao.deleteById(deleteId);
                        if (deleted) {
                            return ServerResponse.success("Member deleted successfully", null);
                        } else {
                            return ServerResponse.error("Delete failed - member not found");
                        }

                    default:
                        return ServerResponse.error("Unknown request type: " + requestType);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.error("Server error: " + e.getMessage());
            }
        }
    }
}