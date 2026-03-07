package com.library.jdbc;

import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcMemberDao implements MemberDao {

    @Override
    public int insert(String name, String address, String phone) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is required");
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("address is required");
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("phone is required");

        String sql = "INSERT INTO member (name, address, phone) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name.trim());
            ps.setString(2, address.trim());
            ps.setString(3, phone.trim());

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
    public Optional<Member> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, name, address, phone FROM member WHERE id = ?";

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
    public List<Member> findAll() throws Exception {
        String sql = "SELECT id, name, address, phone FROM member ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Member> members = new ArrayList<>();

            while (rs.next())
                members.add(mapRow(rs));

            return members;
        }
    }

    @Override
    public Member update(int id, Member member) throws Exception {
        if (id <= 0 || member == null)
            return null;

        String sql = "UPDATE member SET name = ?, address = ?, phone = ? WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setString(3, member.getPhone());
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                // Need to set the ID on the member object
                // You'll need to add setId method to Member class
                try {
                    // Use reflection or add setId method to Member class
                    java.lang.reflect.Field idField = member.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(member, id);
                } catch (Exception e) {
                    // If reflection fails, create a new Member with the ID
                    return new Member(id, member.getName(), member.getAddress(), member.getPhone());
                }
                return member;
            }
            return null;
        }
    }

    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM member WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<Member> findByFilter(java.util.function.Predicate<Member> filter) throws Exception {
        return findAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        return new Member(id, name, address, phone);
    }
}