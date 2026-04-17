package com.library.domain;

import java.util.Arrays;
import java.util.Objects;

public class Member {
    private int id;
    private String name;
    private String address;
    private String phone;

    // F17: New fields for binary file storage
    private String fileName;
    private String contentType;
    private int fileSize;
    private byte[] profileImage;

    // Default constructor (required for Jackson)
    public Member() {}

    // Constructor without binary data (for metadata-only queries)
    public Member(int id, String name, String address, String phone,
                  String fileName, String contentType, int fileSize) {
        this(id, name, address, phone, fileName, contentType, fileSize, null);
    }

    // Full constructor with binary data
    public Member(int id, String name, String address, String phone,
                  String fileName, String contentType, int fileSize, byte[] profileImage) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
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

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }
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