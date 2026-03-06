package com.library.jdbc;

import com.library.domain.Staff;
import com.library.db.DatabaseConnection;
import com.library.dao.StaffDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStaffDao implements StaffDao {

    @Override
    public int insert(String name, String role, String contact) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        if (role == null || role.trim().isEmpty())
            throw new IllegalArgumentException("role is required");
        if (contact == null || contact.trim().isEmpty())
            throw new IllegalArgumentException("contact is required");

        String sql = "INSERT INTO staff (name, role, contact) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name.trim());
            ps.setString(2, role.trim());
            ps.setString(3, contact.trim());

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
    public Optional<Staff> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, name, role, contact FROM staff WHERE id = ?";

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
    public List<Staff> findAll() throws Exception {
        String sql = "SELECT id, name, role, contact FROM staff ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Staff> staffList = new ArrayList<>();

            while (rs.next())
                staffList.add(mapRow(rs));

            return staffList;
        }
    }

    @Override
    public boolean update(int id, String name, String role, String contact) throws Exception {
        if (id <= 0)
            return false;
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        if (role == null || role.trim().isEmpty())
            throw new IllegalArgumentException("role is required");
        if (contact == null || contact.trim().isEmpty())
            throw new IllegalArgumentException("contact is required");

        String sql = "UPDATE staff SET name = ?, role = ?, contact = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, role.trim());
            ps.setString(3, contact.trim());
            ps.setInt(4, id);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM staff WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String role = rs.getString("role");
        String contact = rs.getString("contact");
        return new Staff(id, name, role, contact);
    }
}