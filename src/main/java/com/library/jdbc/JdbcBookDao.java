package com.library.jdbc;

import com.library.domain.Book;
import com.library.db.DatabaseConnection;
import com.library.dao.BookDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookDao implements BookDao {

    @Override
    public int insert(String title, String author, int categoryId, int shelfId, int availableCopies) throws Exception {
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

        String sql = "INSERT INTO book (title, author, category_id, shelf_id, available_copies) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title.trim());
            ps.setString(2, author.trim());
            ps.setInt(3, categoryId);
            ps.setInt(4, shelfId);
            ps.setInt(5, availableCopies);

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed. rows=" + rows);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("No generated key returned");
                return keys.getInt(1);
            }
        }
    }

    @Override
    public Optional<Book> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, title, author, category_id, shelf_id, available_copies FROM book WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                return Optional.of(mapRow(rs));
            }
        }
    }

    @Override
    public List<Book> findAll() throws Exception {
        String sql = "SELECT id, title, author, category_id, shelf_id, available_copies FROM book ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Book> books = new ArrayList<>();

            while (rs.next())
                books.add(mapRow(rs));

            return books;
        }
    }

    @Override
    public boolean update(int id, String title, String author, int categoryId, int shelfId, int availableCopies) throws Exception {
        if (id <= 0)
            return false;
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

        String sql = "UPDATE book SET title = ?, author = ?, category_id = ?, shelf_id = ?, available_copies = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setString(2, author.trim());
            ps.setInt(3, categoryId);
            ps.setInt(4, shelfId);
            ps.setInt(5, availableCopies);
            ps.setInt(6, id);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM book WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        int categoryId = rs.getInt("category_id");
        int shelfId = rs.getInt("shelf_id");
        int availableCopies = rs.getInt("available_copies");
        return new Book(id, title, author, categoryId, shelfId, availableCopies);
    }
}