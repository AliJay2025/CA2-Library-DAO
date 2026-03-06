package com.library.domain;

public class Category {
    private int id;
    private String name;

    public Category(int id, String name) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");

        this.id = id;
        this.name = name.trim();
    }

    public Category(String name) {
        this(0, name);
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}