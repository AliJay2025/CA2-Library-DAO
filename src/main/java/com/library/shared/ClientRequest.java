package com.library.shared;

import java.util.HashMap;
import java.util.Map;

public class ClientRequest
{
    private String _requestType;
    private Map<String, Object> _payload;

    public ClientRequest()
    {
        _requestType = "";
        _payload = new HashMap<>();  // FIXED: Map.of() not available in Java 8
    }

    public String getRequestType() { return _requestType; }
    public void setRequestType(String requestType) { _requestType = requestType; }

    public Map<String, Object> getPayload() { return _payload; }
    public void setPayload(Map<String, Object> payload) { _payload = payload; }

    public String getString(String key)
    {
        Object v = _payload.get(key);
        return v == null ? null : v.toString();
    }

    public int getInt(String key)
    {
        Object v = _payload.get(key);
        if (v == null) return -1;
        try { return Integer.parseInt(v.toString()); }
        catch (NumberFormatException e) { return -1; }
    }
}