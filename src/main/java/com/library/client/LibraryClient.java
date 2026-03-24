package com.library.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * LibraryClient - The client that connects to the server
 *
 * This client communicates with the server using JSON messages over TCP sockets.
 * It provides a menu-driven interface for users to perform CRUD operations.
 *
 * F12-F15: All CRUD operations are implemented here (Get All, Get by ID, Insert, Update, Delete)
 * F16: Error handling is implemented - exceptions are caught and displayed nicely
 */
public class LibraryClient {

    // Server connection details
    private static final String SERVER_HOST = "localhost";  // Server runs on same machine
    private static final int SERVER_PORT = 8080;            // Must match server's port

    // Jackson's ObjectMapper - converts JSON to Java objects and back
    private static ObjectMapper mapper = new ObjectMapper();

    // Scanner for reading user input from console
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Main method - connects to server and displays the menu
     */
    public static void main(String[] args) {
        System.out.println("------------------------------------------");
        System.out.println("  LIBRARY CLIENT - STAGE 2");
        System.out.println("------------------------------------------\n");

        // try-with-resources - automatically closes the socket and streams
        // This establishes a TCP connection to the server
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT + "\n");

            // Main loop - keep showing menu until user chooses Exit
            while (true) {
                displayMenu();                    // Show the menu options
                int choice = getUserChoice();     // Get user's choice (1-5 or 0)

                String request = null;            // Will hold the JSON request

                // Create the appropriate request based on user's choice
                switch (choice) {
                    case 1:
                        request = createGetAllRequest();      // F12: Get all members
                        break;
                    case 2:
                        request = createGetByIdRequest();     // F12: Get member by ID
                        break;
                    case 3:
                        request = createInsertRequest();      // F13: Insert new member
                        break;
                    case 4:
                        request = createUpdateRequest();      // F15: Update member
                        break;
                    case 5:
                        request = createDeleteRequest();      // F14: Delete member
                        break;
                    case 0:
                        System.out.println("\nGoodbye!");
                        return;                    // Exit the program
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        continue;                  // Go back to menu
                }

                if (request != null) {
                    // Send the JSON request to the server
                    out.println(request);
                    System.out.println("\nSent: " + request);

                    // Wait for and receive the JSON response from the server
                    String responseJson = in.readLine();
                    System.out.println("Received: " + responseJson);

                    // Parse the response and display it nicely
                    parseAndDisplayResponse(responseJson);
                }
            }

        } catch (Exception e) {
            // Error handling - display friendly message (F16)
            System.err.println("Error connecting to server: " + e.getMessage());
            System.err.println("Make sure the server is running on port " + SERVER_PORT);
        }
    }

    /**
     * Displays the menu options to the user
     */
    private static void displayMenu() {
        System.out.println("  LIBRARY CLIENT MENU");
        System.out.println("--------------------------------");
        System.out.println("  1. Get All Members");
        System.out.println("  2. Get Member by ID");
        System.out.println("  3. Insert New Member");
        System.out.println("  4. Update Member");
        System.out.println("  5. Delete Member");
        System.out.println("  0. Exit");
        System.out.println("------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Reads user input and returns it as an integer
     *
     * @return user's choice (1-5, 0), or -1 if invalid
     */
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;  // Invalid input (not a number)
        }
    }

    /**
     * Creates JSON request to get all members from the database
     * Format: {"requestType": "GET_ALL_MEMBERS"}
     */
    private static String createGetAllRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "GET_ALL_MEMBERS");
        return mapper.writeValueAsString(request);
    }

    /**
     * Creates JSON request to get a member by ID
     * Format: {"requestType": "GET_MEMBER_BY_ID", "id": 1}
     */
    private static String createGetByIdRequest() throws Exception {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "GET_MEMBER_BY_ID");
        request.put("id", id);

        return mapper.writeValueAsString(request);
    }

    /**
     * Creates JSON request to insert a new member
     * Format: {"requestType": "INSERT_MEMBER", "data": {"name": "...", "address": "...", "phone": "..."}}
     */
    private static String createInsertRequest() throws Exception {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "INSERT_MEMBER");

        // Put member data inside a nested "data" object
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("address", address);
        data.put("phone", phone);
        request.put("data", data);

        return mapper.writeValueAsString(request);
    }

    /**
     * Creates JSON request to update an existing member
     * Format: {"requestType": "UPDATE_MEMBER", "data": {"id": 1, "name": "...", "address": "...", "phone": "..."}}
     */
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

        // Put updated data inside a nested "data" object
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("address", address);
        data.put("phone", phone);
        request.put("data", data);

        return mapper.writeValueAsString(request);
    }

    /**
     * Creates JSON request to delete a member by ID
     * Format: {"requestType": "DELETE_MEMBER", "id": 1}
     */
    private static String createDeleteRequest() throws Exception {
        System.out.print("Enter Member ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "DELETE_MEMBER");
        request.put("id", id);

        return mapper.writeValueAsString(request);
    }

    /**
     * Parses the JSON response from the server and displays it in a user-friendly format

     */
    private static void parseAndDisplayResponse(String responseJson) {
        try {
            // Convert JSON response to a Map (key-value pairs)
            Map<String, Object> response = mapper.readValue(responseJson, Map.class);

            // Extract status and message from the ServerResponse wrapper
            String status = (String) response.get("status");    // "success" or "error"
            String message = (String) response.get("message");  // Human-readable message

            System.out.println("\nServer Response:");
            System.out.println("   Status: " + status);
            System.out.println("   Message: " + message);

            // If successful and there's data, display it
            if ("success".equals(status) && response.get("data") != null) {
                Object data = response.get("data");

                // Case 1: Data is a List of members (from GET_ALL)
                if (data instanceof List) {
                    List<Map<String, Object>> members = (List<Map<String, Object>>) data;
                    System.out.println("    Data: " + members.size() + " member(s) found");
                    for (Map<String, Object> m : members) {
                        System.out.println("      - ID: " + m.get("id") +
                                ", Name: " + m.get("name") +
                                ", Phone: " + m.get("phone"));
                    }
                }
                // Case 2: Data is a single Member (from GET_BY_ID, INSERT, UPDATE)
                else if (data instanceof Map) {
                    Map<String, Object> member = (Map<String, Object>) data;
                    System.out.println("   Data: Member{id=" + member.get("id") +
                            ", name='" + member.get("name") +
                            "', phone='" + member.get("phone") + "'}");
                }
                // Case 3: Data is null (from DELETE operation)
                else if (data == null) {
                    System.out.println("    Data: null (operation completed)");
                }
            }

        } catch (Exception e) {
            // If response parsing fails, show error (F16)
            System.out.println("Error parsing response: " + e.getMessage());
        }
    }
}