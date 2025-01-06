package org.example.frontend.dao;

import org.example.frontend.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDAO {
    private String sql;

    public void save(Message message) {
        String sql;
        if (message.getText() != null) { // Text only
            sql = "INSERT INTO messages (sendAt, conversationID, text) VALUES (?, ?, ?)";
        } else { // Image only (not recommended, consider validation)
            sql = "INSERT INTO messages (sendAt, conversationID, image) VALUES (?, ?, ?)";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, new java.sql.Date(message.getSendAt().getTime()));
            pstmt.setInt(2, message.getConversationId());
            if (message.getText() != null) {
                pstmt.setString(3, message.getText());
            } else {
                pstmt.setString(3, null);
            }
            if (message.getImage() != null) {
                pstmt.setBytes(4, message.getImage());
            } else {
                pstmt.setBytes(4, null);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
