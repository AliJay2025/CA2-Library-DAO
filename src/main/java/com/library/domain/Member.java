package com.library.domain;

import java.util.Arrays;
import java.util.Objects;

public class Member {
    private int id;
    private String name;
    private String address;
    private String phone;
    // F17: New fields for binary file storage
    private String fileName;      // Original filename (e.g., "profile.png")
    private String contentType;   // MIME type (e.g., "image/png")
    private int fileSize;         // Size in bytes
    private byte[] profileImage;  // The actual binary data (BLOB)

    // Default constructor (required for Jackson)
    public Member() {}

    // Simple constructor for testing (without binary data)
    public Member(int id, String name, String address, String phone) {
        this(id, name, address, phone, "", "", 0, null);
    }

    // Constructor without binary data (for metadata-only queries)
    public Member(int id, String name, String address, String phone,
                  String fileName, String contentType, int fileSize) {
        this(id, name, address, phone, fileName, contentType, fileSize, null);
    }

    // Full constructor with validation
    public Member(int id, String name, String address, String phone,
                  String fileName, String contentType, int fileSize, byte[] profileImage) {

        // VALIDATION: ID must be >= 0
        if (id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }

        // VALIDATION: Name cannot be null or empty
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }

        // VALIDATION: Address cannot be null or empty
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("address is required");
        }

        // VALIDATION: Phone cannot be null or empty
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("phone is required");
        }

        this.id = id;
        this.name = name.trim();
        this.address = address.trim();
        this.phone = phone.trim();
        this.fileName = fileName == null ? "" : fileName;
        this.contentType = contentType == null ? "" : contentType;
        this.fileSize = Math.max(0, fileSize);
        this.profileImage = profileImage;
    }

    // Constructor without ID (for insert)
    public Member(String name, String address, String phone) {
        this(0, name, address, phone, "", "", 0, null);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public int getFileSize() { return fileSize; }
    public byte[] getProfileImage() { return profileImage; }

    // Setters with validation
    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("id must be >= 0");
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name.trim();
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("address is required");
        }
        this.address = address.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("phone is required");
        }
        this.phone = phone.trim();
    }

    public void setFileName(String fileName) { this.fileName = fileName == null ? "" : fileName; }
    public void setContentType(String contentType) { this.contentType = contentType == null ? "" : contentType; }
    public void setFileSize(int fileSize) { this.fileSize = Math.max(0, fileSize); }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    // Helper method to check if member has an image
    public boolean hasImage() {
        return profileImage != null && profileImage.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return id == member.id &&
                fileSize == member.fileSize &&
                Objects.equals(name, member.name) &&
                Objects.equals(address, member.address) &&
                Objects.equals(phone, member.phone) &&
                Objects.equals(fileName, member.fileName) &&
                Objects.equals(contentType, member.contentType) &&
                Arrays.equals(profileImage, member.profileImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phone, fileName, contentType, fileSize);
    }

    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', phone='%s', hasImage=%s, fileSize=%d}",
                id, name, phone, hasImage(), fileSize);
    }
}