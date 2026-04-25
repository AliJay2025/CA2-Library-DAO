package com.library.shared;

public enum RequestType
{
    GET_ALL,
    GET_BY_ID,
    INSERT,
    UPDATE,
    DELETE,
    UPLOAD,      // F18: Upload a file for a member
    DOWNLOAD,    // F19: Download a file from a member
    METADATA,    // F20: Get file metadata without downloading
    DISCONNECT
}