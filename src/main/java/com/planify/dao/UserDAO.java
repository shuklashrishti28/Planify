package com.planify.dao;

import com.planify.model.User;
import com.planify.util.DBConnection;
import com.planify.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Contains all JDBC queries for user records. */
public class UserDAO {

    /** Returns an authenticated user, or {@code null} when the credentials do not match. */
    public User authenticateUser(String email, String password) throws SQLException {
        String sql = "SELECT user_id, name, email, role, password FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);

            User user;
            int userId;
            String storedPassword;
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                storedPassword = rs.getString("password");
                if (!PasswordUtil.verify(password, storedPassword)) {
                    return null;
                }
                userId = rs.getInt("user_id");
                user = new User(userId, rs.getString("name"),
                        rs.getString("email"), rs.getString("role"));
            }
            if (!PasswordUtil.isHashed(storedPassword)) {
                updateLegacyPasswordHash(conn, userId, storedPassword, PasswordUtil.hash(password));
            }
            return user;
        }
    }

    private void updateLegacyPasswordHash(Connection conn, int userId, String oldPassword, String passwordHash)
            throws SQLException {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setInt(2, userId);
            stmt.setString(3, oldPassword);
            stmt.executeUpdate();
        }
    }

    /** Inserts a new user account. */
    public boolean addUser(String name, String email, String password, String role) throws SQLException {
        String sql = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, PasswordUtil.hash(password));
            stmt.setString(4, role);

            return stmt.executeUpdate() > 0;
        }
    }

    /** Updates the editable profile fields for an existing user. */
    public boolean updateUser(int id, String name, String email, String role) throws SQLException {
        String sql = "UPDATE Users SET name = ?, email = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, role);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Deletes a user; database foreign-key rules handle dependent records. */
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Returns users without their password values. */
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, name, email, role FROM Users ORDER BY user_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                userList.add(user);
            }
        }
        return userList;
    }

    /** Returns only team-member accounts for task assignment controls. */
    public List<User> getTeamMembers() throws SQLException {
        List<User> members = new ArrayList<>();
        String sql = "SELECT user_id, name, email, role FROM Users WHERE role = 'TEAM_MEMBER' ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                members.add(new User(rs.getInt("user_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("role")));
            }
        }
        return members;
    }

    public boolean isTeamMember(int userId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE user_id = ? AND role = 'TEAM_MEMBER'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
