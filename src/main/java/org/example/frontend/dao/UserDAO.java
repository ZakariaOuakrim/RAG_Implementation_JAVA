package org.example.frontend.dao;

import lombok.Getter;
import lombok.Setter;
import org.example.frontend.model.User;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt; // Add BCrypt library for hashing

public class UserDAO {

    public int login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (BCrypt.checkpw(password, storedHash)) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void signup(User user) throws SQLException {
        String sql = "INSERT INTO users (firstName, lastName, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    public boolean userExist(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If a row is returned, the user exists
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow the exception to propagate it
        }

    }
}