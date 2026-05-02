package com.library.shared;

/**
 * Generic wrapper for all server responses
 * Contains status, message, and data
 * F11: All server replies use this wrapper
 */
public class ServerResponse<T>
{
    private String _status;   // "success" or "error"
    private String _message;  // Human-readable message
    private T _data;          // The actual data (Member, List, etc.)

    public ServerResponse(String status, String message, T data)
    {
        // Java 8 compatible - trim().isEmpty() instead of isBlank()
        if (status == null || status.trim().isEmpty())
            throw new IllegalArgumentException("status is required");

        _status = status;
        _message = message;
        _data = data;
    }

    public String getStatus() { return _status; }
    public String getMessage() { return _message; }
    public T getData() { return _data; }

    // Create a success response
    public static <T> ServerResponse<T> success(String message, T data)
    {
        return new ServerResponse<>("success", message, data);
    }

    // Create an error response
    public static <T> ServerResponse<T> error(String message)
    {
        return new ServerResponse<>("error", message, null);
    }

    // Check if response is successful
    public boolean isSuccess()
    {
        return "success".equals(_status);
    }
}