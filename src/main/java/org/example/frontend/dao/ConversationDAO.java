package org.example.frontend.dao;

import org.example.frontend.model.Conversation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
