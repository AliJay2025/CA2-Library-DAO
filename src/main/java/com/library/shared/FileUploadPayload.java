package com.library.shared;

public class FileUploadPayload {

    private int entityId;        // ID of the member this file belongs to
    private String fileName;     // Original filename with extension
    private String contentType;  // MIME type (image/png, etc.)
    private int fileSize;        // Size in bytes (before Base64 encoding)
    private String fileData;     // Base64-encoded binary data

    // Default constructor (required for Jackson)
    public FileUploadPayload() {}

    // Constructor with all fields
    public FileUploadPayload(int entityId, String fileName,
                             String contentType, int fileSize, String fileData) {
        this.entityId = entityId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
    }

    // Getters and Setters
    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public int getFileSize() { return fileSize; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }

    public String getFileData() { return fileData; }
    public void setFileData(String fileData) { this.fileData = fileData; }

    @Override
    public String toString() {
        return "FileUploadPayload{entityId=" + entityId +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize + '}';
    }
}
