package com.library.jdbc;

import com.library.domain.Category;
import com.library.db.DatabaseConnection;
import com.library.dao.CategoryDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCategoryDao implements CategoryDao {

    @Override
    public int insert(String name) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");

        String sql = "INSERT INTO category (name) VALUES (?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name.trim());

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
    public Optional<Category> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, name FROM category WHERE id = ?";

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
    public List<Category> findAll() throws Exception {
        String sql = "SELECT id, name FROM category ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Category> categories = new ArrayList<>();

            while (rs.next())
                categories.add(mapRow(rs));

            return categories;
        }
    }

    @Override
    public boolean update(int id, String name) throws Exception {
        if (id <= 0)
            return false;
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");

        String sql = "UPDATE category SET name = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setInt(2, id);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM category WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Category(id, name);
    }
}