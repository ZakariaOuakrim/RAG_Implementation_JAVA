package org.example.frontend.dao;

import org.example.frontend.model.Message;
import org.example.frontend.model.Sender;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void save(Message message) {
        String sql;
        boolean hasText = message.getText() != null;
        boolean hasImage = message.getImage() != null;

        // Determine the base SQL query, accounting for text, image, and sender
        if (hasText && hasImage) {
            sql = "INSERT INTO message (sendAt, conversationID, text, image, sender) VALUES (?, ?, ?, ?, ?)";
        } else if (hasText) {
            sql = "INSERT INTO message (sendAt, conversationID, text, sender) VALUES (?, ?, ?, ?)";
        } else if (hasImage) {
            sql = "INSERT INTO message (sendAt, conversationID, image, sender) VALUES (?, ?, ?, ?)";
        } else {
            throw new IllegalArgumentException("Message must contain either text or image.");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the common parameters
            pstmt.setDate(1, new java.sql.Date(message.getSendAt().getTime()));
            pstmt.setInt(2, message.getConversationId());

            // Set the message content and sender based on which fields are present
            if (hasText && hasImage) {
                pstmt.setString(3, message.getText());
                pstmt.setBytes(4, message.getImage());
                pstmt.setString(5, message.getSender().name());  // Store sender as a String (e.g., "BOT" or "USER")
            } else if (hasText) {
                pstmt.setString(3, message.getText());
                pstmt.setString(4, message.getSender().name());  // Store sender as a String
            } else if (hasImage) {
                pstmt.setBytes(3, message.getImage());
                pstmt.setString(4, message.getSender().name());  // Store sender as a String
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Message> getAllMessages(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);

            try (var resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setId(resultSet.getInt("id"));
                    message.setSendAt(resultSet.getDate("sendAt"));
                    message.setConversationId(resultSet.getInt("conversationID"));
                    message.setText(resultSet.getString("text"));
                    message.setImage(resultSet.getBytes("image"));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    //function to know wach nsaviw had conversation ola la only if >=2
    public int getMessageCount(int conversationId) {
        String sql = "SELECT COUNT(*) FROM message WHERE conversationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);

            try (var resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public List<Message> getMessagesByConversationId(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversationID = ? ORDER BY sendAt ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date sendAt = rs.getDate("sendAt");
                    int conversationIdFromDb = rs.getInt("conversationID");
                    String text = rs.getString("text");
                    byte[] image = rs.getBytes("image");
                    String senderStr = rs.getString("sender");
                    // hna I convert mn String l Sender enum
                    Sender sender = Sender.valueOf(senderStr);

                    Message message = new Message(id, sendAt, conversationIdFromDb, text, image, sender);
                    messages.add(message);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching messages for conversation ID: " + conversationId, e);
        }

        return messages;
    }


}
