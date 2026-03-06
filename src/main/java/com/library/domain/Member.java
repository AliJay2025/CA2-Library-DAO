package com.library.domain;

public class Member {
    private int id;
    private String name;
    private String address;
    private String phone;

    public Member(int id, String name, String address, String phone) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("name is required");
        if (address == null || address.isEmpty())
            throw new IllegalArgumentException("address is required");
        if (phone == null || phone.isEmpty())
            throw new IllegalArgumentException("phone is required");

        this.id = id;
        this.name = name.trim();
        this.address = address.trim();
        this.phone = phone.trim();
    }

    public Member(String name, String address, String phone) {
        this(0, name, address, phone);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + "', phone='" + phone + "'}";
    }
}