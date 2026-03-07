package com.library.domain;

public class Member {
    private int id;
    private String name;
    private String address;
    private String phone;

    // Default constructor (required for Jackson)
    public Member() {}

    public Member(int id, String name, String address, String phone) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("address is required");
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("phone is required");

        this.id = id;
        this.name = name.trim();
        this.address = address.trim();
        this.phone = phone.trim();
    }

    //<editor-fold desc="Constructor">
    public Member(String name, String address, String phone) {
        this(0, name, address, phone);
    }
    //</editor-fold>

    //<editor-fold desc="Getter and Setters">
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    // Setters (required for Jackson deserialization)
    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("id must be >= 0");
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        this.name = name.trim();
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("address is required");
        this.address = address.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("phone is required");
        this.phone = phone.trim();
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + "', phone='" + phone + "'}";
    }
}