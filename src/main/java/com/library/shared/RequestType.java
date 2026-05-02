package com.library.shared;

// All possible request types the client can send to the server

public enum RequestType
{
    GET_ALL,
    GET_BY_ID,
    INSERT,
    UPDATE,
    DELETE,
    UPLOAD,       // Upload a file/image for a member - F18
    DOWNLOAD,     // Download a file/image from a member - F19
    METADATA,     // Get file metadata without downloading the file - F20
    DISCONNECT    // Tell server client is closing connection - F21
}