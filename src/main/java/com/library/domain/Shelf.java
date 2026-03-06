package com.library.domain;

public class Shelf {
    private int id;
    private String shelfNumber;
    private String location;

    public Shelf(int id, String shelfNumber, String location) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (shelfNumber == null || shelfNumber.trim().isEmpty())
            throw new IllegalArgumentException("shelfNumber is required");
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("location is required");

        this.id = id;
        this.shelfNumber = shelfNumber.trim();
        this.location = location.trim();
    }

    public Shelf(String shelfNumber, String location) {
        this(0, shelfNumber, location);
    }

    public int getId() { return id; }
    public String getShelfNumber() { return shelfNumber; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return "Shelf{id=" + id + ", number='" + shelfNumber + "', location='" + location + "'}";
    }
}