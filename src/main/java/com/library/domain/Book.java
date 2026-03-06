package com.library.domain;

public class Book {
    private int id;
    private String title;
    private String author;
    private int categoryId;
    private int shelfId;
    private int availableCopies;

    public Book(int id, String title, String author, int categoryId, int shelfId, int availableCopies) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("title is required");
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("author is required");
        if (categoryId <= 0)
            throw new IllegalArgumentException("categoryId must be > 0");
        if (shelfId <= 0)
            throw new IllegalArgumentException("shelfId must be > 0");
        if (availableCopies < 0)
            throw new IllegalArgumentException("availableCopies cannot be negative");

        this.id = id;
        this.title = title.trim();
        this.author = author.trim();
        this.categoryId = categoryId;
        this.shelfId = shelfId;
        this.availableCopies = availableCopies;
    }

    public Book(String title, String author, int categoryId, int shelfId, int availableCopies) {
        this(0, title, author, categoryId, shelfId, availableCopies);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCategoryId() { return categoryId; }
    public int getShelfId() { return shelfId; }
    public int getAvailableCopies() { return availableCopies; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "'}";
    }
}