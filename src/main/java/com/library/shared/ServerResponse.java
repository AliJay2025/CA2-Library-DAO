package com.library.shared;

public class ServerResponse<T>
{
    // === Fields ===
    private String _status;
    private String _message;
    private T _data;

    // === Constructors ===
    public ServerResponse(String status, String message, T data)
    {
        if (status == null || status.isBlank())
            throw new IllegalArgumentException("status is required");

        _status = status;
        _message = message;
        _data = data;
    }

    // === Properties ===
    public String getStatus() { return _status; }
    public String getMessage() { return _message; }
    public T getData() { return _data; }

    // === Methods ===
    // CHANGE: Use "success" instead of "OK" to match client
    public static <T> ServerResponse<T> success(String message, T data)
    {
        return new ServerResponse<>("success", message, data);
    }

    public static <T> ServerResponse<T> error(String message)
    {
        return new ServerResponse<>("error", message, null);
    }

    public boolean isSuccess()
    {
        return "success".equals(_status);
    }
}