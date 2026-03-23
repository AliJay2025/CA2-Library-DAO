package com.library.model;

public class ServerResponse<T> {
    private String status;   // "success" or "error"
    private String message;  // Human-readable message
    private T data;          // The actual data (Member, List<Member>, etc.)

    // Default constructor (required for JSON deserialization)
    public ServerResponse() {}

    // Constructor
    public ServerResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Factory method for success response
    public static <T> ServerResponse<T> success(String message, T data) {
        return new ServerResponse<>("success", message, data);
    }

    // Factory method for error response
    public static <T> ServerResponse<T> error(String message) {
        return new ServerResponse<>("error", message, null);
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    @Override
    public String toString() {
        return "ServerResponse{status='" + status + "', message='" + message + "', data=" + data + "}";
    }
}