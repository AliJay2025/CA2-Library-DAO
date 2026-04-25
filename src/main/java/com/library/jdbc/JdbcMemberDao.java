package com.library.jdbc;

import com.library.dao.MemberDao;
import com.library.domain.Member;
import com.library.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberDao implements MemberDao {

    // F17: Insert member with image
    @Override
    public int insertMemberWithImage(Member member) throws Exception {
        String sql = "INSERT INTO member (name, address, phone, file_name, content_type, file_size, profile_image) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getFileName());
            ps.setString(5, member.getContentType());
            ps.setInt(6, member.getFileSize());
            ps.setBytes(7, member.getProfileImage());

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed. rows=" + rows);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    member.setId(generatedId);
                    return generatedId;
                }
                throw new IllegalStateException("No generated key returned");
            }
        }
    }

    // F17: Regular insert without image (for backward compatibility)
    @Override
    public int insert(String name, String address, String phone) throws Exception {
        Member m = new Member(name, address, phone);
        return insertMemberWithImage(m);
    }

    // F17: Find by ID including image
    @Override
    public Optional<Member> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, name, address, phone, file_name, content_type, file_size, profile_image "
                + "FROM member WHERE id = ?";

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

    // F20: Find metadata only (no BLOB)
    @Override
    public Optional<Member> findMetadataById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, name, address, phone, file_name, content_type, file_size "
                + "FROM member WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                Member m = new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("file_name"),
                        rs.getString("content_type"),
                        rs.getInt("file_size"),
                        null
                );
                return Optional.of(m);
            }
        }
    }

    // F17: Find all (with image - careful for performance)
    @Override
    public List<Member> findAll() throws Exception {
        String sql = "SELECT id, name, address, phone, file_name, content_type, file_size, profile_image "
                + "FROM member ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Member> members = new ArrayList<>();

            while (rs.next())
                members.add(mapRow(rs));

            return members;
        }
    }

    // F20: Find all metadata only (no BLOB) - better performance
    @Override
    public List<Member> findAllMetadataOnly() throws Exception {
        String sql = "SELECT id, name, address, phone, file_name, content_type, file_size "
                + "FROM member ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Member> members = new ArrayList<>();

            while (rs.next()) {
                Member m = new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("file_name"),
                        rs.getString("content_type"),
                        rs.getInt("file_size"),
                        null
                );
                members.add(m);
            }
            return members;
        }
    }

    // F17: Update member including image - FIXED to return Member
    @Override
    public Member update(int id, Member member) throws Exception {
        String sql = "UPDATE member SET name = ?, address = ?, phone = ?, "
                + "file_name = ?, content_type = ?, file_size = ?, profile_image = ? "
                + "WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getFileName());
            ps.setString(5, member.getContentType());
            ps.setInt(6, member.getFileSize());
            ps.setBytes(7, member.getProfileImage());
            ps.setInt(8, id);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                member.setId(id);
                return member;
            }
            return null;
        }
    }

    // Delete by ID
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

    // Map full row including BLOB
    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("file_name"),
                rs.getString("content_type"),
                rs.getInt("file_size"),
                rs.getBytes("profile_image")
        );
    }
}