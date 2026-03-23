package com.library.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LibraryClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static ObjectMapper mapper = new ObjectMapper();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("------------------------------------------");
        System.out.println("  LIBRARY CLIENT - STAGE 2");
        System.out.println("------------------------------------------\n");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT + "\n");

            while (true) {
                displayMenu();
                int choice = getUserChoice();

                String request = null;

                switch (choice) {
                    case 1:
                        request = createGetAllRequest();
                        break;
                    case 2:
                        request = createGetByIdRequest();
                        break;
                    case 3:
                        request = createInsertRequest();
                        break;
                    case 4:
                        request = createUpdateRequest();
                        break;
                    case 5:
                        request = createDeleteRequest();
                        break;
                    case 0:
                        System.out.println("\nGoodbye!");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        continue;
                }

                if (request != null) {
                    // Send request to server
                    out.println(request);
                    System.out.println("\n📤 Sent: " + request);

                    // Receive response from server
                    String responseJson = in.readLine();
                    System.out.println("Received: " + responseJson);

                    // Parse and display response
                    parseAndDisplayResponse(responseJson);
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.err.println("Make sure the server is running on port " + SERVER_PORT);
        }
    }

    private static void displayMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("  LIBRARY CLIENT MENU");
        System.out.println("--------------------------------------------------");
        System.out.println("  1. Get All Members");
        System.out.println("  2. Get Member by ID");
        System.out.println("  3. Insert New Member");
        System.out.println("  4. Update Member");
        System.out.println("  5. Delete Member");
        System.out.println("  0. Exit");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String createGetAllRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "GET_ALL_MEMBERS");
        return mapper.writeValueAsString(request);
    }

    private static String createGetByIdRequest() throws Exception {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "GET_MEMBER_BY_ID");
        request.put("id", id);

        return mapper.writeValueAsString(request);
    }

    private static String createInsertRequest() throws Exception {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "INSERT_MEMBER");

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("address", address);
        data.put("phone", phone);
        request.put("data", data);

        return mapper.writeValueAsString(request);
    }

    private static String createUpdateRequest() throws Exception {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter New Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter New Phone: ");
        String phone = scanner.nextLine();

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "UPDATE_MEMBER");

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("address", address);
        data.put("phone", phone);
        request.put("data", data);

        return mapper.writeValueAsString(request);
    }

    private static String createDeleteRequest() throws Exception {
        System.out.print("Enter Member ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "DELETE_MEMBER");
        request.put("id", id);

        return mapper.writeValueAsString(request);
    }

    @SuppressWarnings("unchecked")
    private static void parseAndDisplayResponse(String responseJson) {
        try {
            Map<String, Object> response = mapper.readValue(responseJson, Map.class);

            String status = (String) response.get("status");
            String message = (String) response.get("message");

            System.out.println("\nServer Response:");
            System.out.println("   Status: " + status);
            System.out.println("   Message: " + message);

            if ("success".equals(status) && response.get("data") != null) {
                Object data = response.get("data");

                // Check if data is a list (multiple members)
                if (data instanceof List) {
                    List<Map<String, Object>> members = (List<Map<String, Object>>) data;
                    System.out.println("   Data: " + members.size() + " member(s) found");
                    for (Map<String, Object> m : members) {
                        System.out.println("      - ID: " + m.get("id") + ", Name: " + m.get("name") + ", Phone: " + m.get("phone"));
                    }
                }
                // Check if data is a single member
                else if (data instanceof Map) {
                    Map<String, Object> member = (Map<String, Object>) data;
                    System.out.println("   Data: Member{id=" + member.get("id") +
                            ", name='" + member.get("name") +
                            "', phone='" + member.get("phone") + "'}");
                }
                // Check if data is null (delete operation)
                else if (data == null) {
                    System.out.println("Data: null (operation completed)");
                }
            }

        } catch (Exception e) {
            System.out.println("Error parsing response: " + e.getMessage());
        }
    }
}