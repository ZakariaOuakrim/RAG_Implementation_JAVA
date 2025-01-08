package org.example.frontend.dao;

import org.example.frontend.model.Conversation;
import org.example.frontend.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {
    public List<Conversation>getAllConversations(int userID) throws SQLException {
        List<Conversation> conversations = new ArrayList<Conversation>();
        String sql = "select * from conversation where userID = " + userID;
        try(Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                Conversation conversation=new Conversation(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("userID"),
                        rs.getDate("started_at")
                );
                conversations.add(conversation);
            }
        }
        return conversations;
    }

    public List<String> getAllConversationTitles(int userID) {
        List<String> titles = new  ArrayList<String>();
        String sql = "select title from conversation where userID = " + userID;
        try(Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return titles;
    }

    public int createConversation(Conversation conversation) {
        String sql = "INSERT INTO conversation (title, userID, started_at) VALUES (?, ?, ?)";
        int generatedId = -1; // to store the generated ID of the new conversation
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, conversation.getTitle());
            pstmt.setInt(2, conversation.getUserID());
            pstmt.setDate(3, conversation.getDate());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating conversation", e);
        }

        return generatedId; //
    }

    public void deleteConversation(int conversationID) {
        String deleteMessagesSql = "DELETE FROM message WHERE conversationID = ?";
        String deleteConversationSql = "DELETE FROM conversation WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Disable auto-commit to ensure both operations are part of a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement deleteMessagesStmt = conn.prepareStatement(deleteMessagesSql);
                 PreparedStatement deleteConversationStmt = conn.prepareStatement(deleteConversationSql)) {

                // First, delete the messages associated with the conversation
                deleteMessagesStmt.setInt(1, conversationID);
                deleteMessagesStmt.executeUpdate();

                // Then, delete the conversation itself
                deleteConversationStmt.setInt(1, conversationID);
                int rowsAffected = deleteConversationStmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new RuntimeException("Conversation with ID " + conversationID + " not found.");
                }

                // Commit the transaction
                conn.commit();

            } catch (SQLException e) {
                // Rollback in case of an error
                conn.rollback();
                throw new RuntimeException("Error deleting conversation and related messages", e);
            } finally {
                // Re-enable auto-commit
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error with database connection", e);
        }
    }

    public int getConversationIdByTitle(String title, int userID) {
        String sql = "SELECT id FROM conversation WHERE title = ? AND userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setInt(2, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new RuntimeException("Conversation with title '" + title + "' not found.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching conversation ID by title", e);
        }
    }

}
