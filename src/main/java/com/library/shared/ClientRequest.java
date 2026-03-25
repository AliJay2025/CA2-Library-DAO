package com.library.shared;

import java.util.Map;

/**
 * Represents a request sent from the client to the server.
 * Contains a request type and an optional payload map.
 */
public class ClientRequest
{
    // === Fields ===
    private String _requestType;
    private Map<String, Object> _payload;

    // === Constructors ===
    /**
     * Creates an empty ClientRequest — needed by Jackson deserialisation.
     */
    public ClientRequest()
    {
        _requestType = "";
        _payload = Map.of();
    }

    // === Public API ===
    /**
     * Gets the request type string (e.g. "GET_ALL").
     *
     * @return The request type.
     */
    public String getRequestType()
    {
        return _requestType;
    }

    /**
     * Sets the request type.
     *
     * @param requestType The request type to set.
     */
    public void setRequestType(String requestType)
    {
        _requestType = requestType;
    }

    /**
     * Gets the payload map.
     *
     * @return The payload map.
     */
    public Map<String, Object> getPayload()
    {
        return _payload;
    }

    /**
     * Sets the payload map.
     *
     * @param payload The payload map to set.
     */
    public void setPayload(Map<String, Object> payload)
    {
        _payload = payload;
    }

    /**
     * Gets a string value from the payload by key, or null if absent.
     *
     * @param key The key to look up.
     * @return The string value, or null.
     */
    public String getString(String key)
    {
        Object v = _payload.get(key);
        return v == null ? null : v.toString();
    }

    /**
     * Gets an integer value from the payload by key, or -1 if absent/unparseable.
     *
     * @param key The key to look up.
     * @return The integer value, or -1 if not found.
     */
    public int getInt(String key)
    {
        Object v = _payload.get(key);
        if (v == null)
            return -1;
        try
        {
            return Integer.parseInt(v.toString());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
}