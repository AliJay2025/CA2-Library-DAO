package com.library.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.shared.ClientRequest;
import com.library.shared.RequestType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Client that connects to LibraryServer and sends JSON requests
 */
public class LibraryClient
{
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception
    {
        System.out.println("------------------------------------------");
        System.out.println("  LIBRARY CLIENT - STAGE 3");
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
                    case 1: request = createRequest(RequestType.GET_ALL, null); break;      // F3
                    case 2: request = createGetByIdRequest(); break;                       // F4
                    case 3: request = createInsertRequest(); break;                        // F6
                    case 4: request = createUpdateRequest(); break;                        // F7
                    case 5: request = createDeleteRequest(); break;                        // F5
                    case 6: request = createUploadRequest(); break;                        // F18
                    case 7: request = createDownloadRequest(); break;                      // F19
                    case 8: request = createMetadataRequest(); break;                      // F20
                    case 0: System.out.println("\nGoodbye!"); return;                      // F21
                    default: System.out.println("\nInvalid choice."); continue;
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

    // Display menu options to user
    private static void displayMenu()
    {
        System.out.println("\n  LIBRARY CLIENT MENU");
        System.out.println("--------------------------------");
        System.out.println("  1. Get All Members (F3)");
        System.out.println("  2. Get Member by ID (F4)");
        System.out.println("  3. Insert New Member (F6)");
        System.out.println("  4. Update Member (F7)");
        System.out.println("  5. Delete Member (F5)");
        System.out.println("  6. Upload Profile Image (F18)");
        System.out.println("  7. Download Profile Image (F19)");
        System.out.println("  8. Get File Metadata (F20)");
        System.out.println("  0. Exit (F21)");
        System.out.println("------------------------------");
        System.out.print("Enter your choice: ");
    }

    // Read user input as integer
    private static int getUserChoice()
    {
        try { return Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { return -1; }
    }

    // Create a generic request
    private static ClientRequest createRequest(RequestType type, Map<String, Object> payload)
    {
        ClientRequest request = new ClientRequest();
        request.setRequestType(type.name());
        request.setPayload(payload);
        return request;
    }

    // F4: Create request to get member by ID
    private static ClientRequest createGetByIdRequest()
    {
        System.out.print("Enter Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        return createRequest(RequestType.GET_BY_ID, payload);
    }

    // F6: Create request to insert new member
    private static ClientRequest createInsertRequest()
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

    // F7: Create request to update member
    private static ClientRequest createUpdateRequest()
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

    // F5: Create request to delete member
    private static ClientRequest createDeleteRequest()
    {
        System.out.print("Enter Member ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        return createRequest(RequestType.DELETE, payload);
    }

    // F18: Create request to upload file - reads file, converts to Base64
    private static ClientRequest createUploadRequest() throws Exception
    {
        System.out.print("Enter Member ID to upload image for: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter image file path (e.g., profile.png): ");
        String filePath = scanner.nextLine();

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("   File not found: " + filePath);
            return null;
        }

        // Read file bytes
        byte[] fileBytes = Files.readAllBytes(path);

        // Convert to Base64 string
        String base64Data = Base64.getEncoder().encodeToString(fileBytes);

        // Get file metadata
        String fileName = path.getFileName().toString();
        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "application/octet-stream";
        int fileSize = fileBytes.length;

        System.out.println("   File: " + fileName + " (" + fileSize + " bytes)");
        System.out.println("   Type: " + contentType);

        // Build payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("entityId", id);
        payload.put("fileName", fileName);
        payload.put("contentType", contentType);
        payload.put("fileSize", fileSize);
        payload.put("fileData", base64Data);

        return createRequest(RequestType.UPLOAD, payload);
    }

    // F19: Create request to download file
    private static ClientRequest createDownloadRequest() throws Exception
    {
        System.out.print("Enter Member ID to download image for: ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);

        return createRequest(RequestType.DOWNLOAD, payload);
    }

    // F20: Create request to get metadata only (no file download)
    private static ClientRequest createMetadataRequest() throws Exception
    {
        System.out.print("Enter Member ID (or 0 for all): ");
        int id = Integer.parseInt(scanner.nextLine());

        Map<String, Object> payload = new HashMap<>();
        if (id == 0) {
            payload.put("all", true);
        } else {
            payload.put("id", id);
        }

        return createRequest(RequestType.METADATA, payload);
    }

    // Send request and print response
    private static void sendAndPrint(PrintWriter out, BufferedReader in, ObjectMapper mapper, ClientRequest request) throws Exception
    {
        String response = sendAndReceive(out, in, mapper, request);
        parseAndDisplayResponse(response, mapper);
    }

    // Send request and receive response
    private static String sendAndReceive(PrintWriter out, BufferedReader in, ObjectMapper mapper, ClientRequest request) throws Exception
    {
        String json = mapper.writeValueAsString(request);
        System.out.println("\n📤 Sent: " + json);
        out.println(json);

        String response = in.readLine();
        System.out.println("📥 Received: " + response);
        return response;
    }

    // Parse server response and display appropriately
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

                // F19: Check if this is a file download response
                if (data.has("fileData") && data.has("fileName"))
                {
                    String fileName = data.get("fileName").asText();
                    String fileData = data.get("fileData").asText();
                    int fileSize = data.get("fileSize").asInt();

                    System.out.println("   Downloading: " + fileName + " (" + fileSize + " bytes)");

                    // Decode Base64 back to bytes
                    byte[] imageBytes = Base64.getDecoder().decode(fileData);

                    // Save to disk
                    Path downloadDir = Paths.get("downloads");
                    if (!Files.exists(downloadDir)) {
                        Files.createDirectories(downloadDir);
                    }

                    Path downloadPath = downloadDir.resolve(fileName);
                    Files.write(downloadPath, imageBytes);

                    System.out.println("   File saved to: " + downloadPath.toAbsolutePath());
                }
                else
                {
                    System.out.println("   Data: " + data.toString());
                }
            }
            else if ("success".equals(status) && (!root.has("data") || root.get("data").isNull()))
            {
                System.out.println("   Data: null (operation completed)");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error parsing response: " + e.getMessage());
        }
    }
}