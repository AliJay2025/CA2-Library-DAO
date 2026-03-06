package com.library.domain;

public class Staff {
    private int id;
    private String name;
    private String role;
    private String contact;

    public Staff(int id, String name, String role, String contact) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        if (role == null || role.trim().isEmpty())
            throw new IllegalArgumentException("role is required");
        if (contact == null || contact.trim().isEmpty())
            throw new IllegalArgumentException("contact is required");

        this.id = id;
        this.name = name.trim();
        this.role = role.trim();
        this.contact = contact.trim();
    }

    public Staff(String name, String role, String contact) {
        this(0, name, role, contact);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return "Staff{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}