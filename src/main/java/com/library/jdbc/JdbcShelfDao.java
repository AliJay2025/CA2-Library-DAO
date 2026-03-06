package com.library.jdbc;

import com.library.domain.Shelf;
import com.library.db.DatabaseConnection;
import com.library.dao.ShelfDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcShelfDao implements ShelfDao {

    @Override
    public int insert(String shelfNumber, String location) throws Exception {
        if (shelfNumber == null || shelfNumber.trim().isEmpty())
            throw new IllegalArgumentException("shelfNumber is required");
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("location is required");

        String sql = "INSERT INTO shelf (shelf_number, location) VALUES (?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, shelfNumber.trim());
            ps.setString(2, location.trim());

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
    public Optional<Shelf> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, shelf_number, location FROM shelf WHERE id = ?";

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
    public List<Shelf> findAll() throws Exception {
        String sql = "SELECT id, shelf_number, location FROM shelf ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Shelf> shelves = new ArrayList<>();

            while (rs.next())
                shelves.add(mapRow(rs));

            return shelves;
        }
    }

    @Override
    public boolean update(int id, String shelfNumber, String location) throws Exception {
        if (id <= 0)
            return false;
        if (shelfNumber == null || shelfNumber.trim().isEmpty())
            throw new IllegalArgumentException("shelfNumber is required");
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("location is required");

        String sql = "UPDATE shelf SET shelf_number = ?, location = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, shelfNumber.trim());
            ps.setString(2, location.trim());
            ps.setInt(3, id);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM shelf WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Shelf mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String shelfNumber = rs.getString("shelf_number");
        String location = rs.getString("location");
        return new Shelf(id, shelfNumber, location);
    }
}