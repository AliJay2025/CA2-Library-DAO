package com.library.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Console client that communicates with the LibraryServer using JSON protocol.
 * User can choose options from a menu to perform CRUD operations.
 *
 * F12-F15: All CRUD operations are implemented
 * F16: Error handling is implemented
 */
public class LibraryClient
{
    // === Static Fields ===
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);

    // === Methods ===
    /**
     * Entry point for the client program.
     *
     * @param args Command-line arguments.
     * @throws Exception If communication fails.
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println("------------------------------------------");
        System.out.println("  LIBRARY CLIENT - STAGE 2");
        System.out.println("------------------------------------------\n");

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
        {
            System.out.println("Connected to server at " + HOST + ":" + PORT + "\n");

            while (true)
            {
                displayMenu();
                int choice = getUserChoice();

                ClientRequest request = null;

                switch (choice)
                {
                    case 1:
                        request = createRequest(RequestType.GET_ALL, null);
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

                if (request != null)
                {
                    sendAndPrint(out, in, mapper, request);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.err.println("Make sure the server is running on port " + PORT);
        }
    }

    // === Menu Methods ===
    private static void displayMenu()
    {
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

    private static int getUserChoice()
    {
        try
        {
            return Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    // === Request Creation Methods ===
    private static ClientRequest createRequest(RequestType type, Map<String, Object> payload)
    {
        ClientRequest request = new ClientRequest();
        request.setRequestType(type.name());
        request.setPayload(payload);
        return request;
    }

    private static ClientRequest createGetByIdRequest() throws Exception
    {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);

        return createRequest(RequestType.GET_BY_ID, payload);
    }

    private static ClientRequest createInsertRequest() throws Exception
    {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("address", address);
        payload.put("phone", phone);

        return createRequest(RequestType.INSERT, payload);
    }

    private static ClientRequest createUpdateRequest() throws Exception
    {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter New Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter New Phone: ");
        String phone = scanner.nextLine();

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.put("name", name);
        payload.put("address", address);
        payload.put("phone", phone);

        return createRequest(RequestType.UPDATE, payload);
    }

    private static ClientRequest createDeleteRequest() throws Exception
    {
        System.out.print("Enter Member ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);

        return createRequest(RequestType.DELETE, payload);
    }

    // === Communication Methods ===
    /**
     * Sends a request and prints the response.
     *
     * @param out Writer for socket output.
     * @param in Reader for socket input.
     * @param mapper Jackson mapper.
     * @param request The request to send.
     * @throws Exception If communication fails.
     */
    private static void sendAndPrint(PrintWriter out, BufferedReader in, ObjectMapper mapper, ClientRequest request) throws Exception
    {
        String response = sendAndReceive(out, in, mapper, request);
        parseAndDisplayResponse(response, mapper);
    }

    /**
     * Sends a request and returns the response string.
     *
     * @param out Writer for socket output.
     * @param in Reader for socket input.
     * @param mapper Jackson mapper.
     * @param request The request to send.
     * @return The raw JSON response string.
     * @throws Exception If communication fails.
     */
    private static String sendAndReceive(PrintWriter out, BufferedReader in, ObjectMapper mapper, ClientRequest request) throws Exception
    {
        String json = mapper.writeValueAsString(request);

        System.out.println("\n Sent: " + json);
        out.println(json);

        String response = in.readLine();
        System.out.println(" Received: " + response);

        return response;
    }

    /**
     * Parses the JSON response from the server and displays it in a user-friendly format.
     *
     * @param responseJson The raw JSON response.
     * @param mapper Jackson mapper.
     */
    private static void parseAndDisplayResponse(String responseJson, ObjectMapper mapper)
    {
        try
        {
            JsonNode root = mapper.readTree(responseJson);
            String status = root.get("status").asText();
            String message = root.get("message").asText();

            System.out.println("\n📋 Server Response:");
            System.out.println("   Status: " + status);
            System.out.println("   Message: " + message);

            if ("success".equals(status) && root.has("data") && !root.get("data").isNull())
            {
                JsonNode data = root.get("data");

                // Case 1: Data is an array (GET_ALL)
                if (data.isArray())
                {
                    System.out.println("   📊 Data: " + data.size() + " member(s) found");
                    for (JsonNode member : data)
                    {
                        System.out.println("      - ID: " + member.get("id").asInt() +
                                ", Name: " + member.get("name").asText() +
                                ", Phone: " + member.get("phone").asText());
                    }
                }
                // Case 2: Data is a single object (GET_BY_ID, INSERT, UPDATE)
                else if (data.isObject())
                {
                    System.out.println("   👤 Data: Member{id=" + data.get("id").asInt() +
                            ", name='" + data.get("name").asText() +
                            "', phone='" + data.get("phone").asText() + "'}");
                }
            }
            else if ("success".equals(status) && (!root.has("data") || root.get("data").isNull()))
            {
                System.out.println("Data: null (operation completed)");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error parsing response: " + e.getMessage());
        }
    }
}