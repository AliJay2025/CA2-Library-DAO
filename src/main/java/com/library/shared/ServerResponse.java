package com.library.shared;

public class ServerResponse<T>
{
    private String _status;
    private String _message;
    private T _data;

    public ServerResponse(String status, String message, T data)
    {
        if (status == null || status.trim().isEmpty())
            throw new IllegalArgumentException("status is required");

        _status = status;
        _message = message;
        _data = data;
    }

    public String getStatus() { return _status; }
    public String getMessage() { return _message; }
    public T getData() { return _data; }

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