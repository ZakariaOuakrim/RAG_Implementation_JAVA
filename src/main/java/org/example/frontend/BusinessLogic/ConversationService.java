package org.example.frontend.BusinessLogic;

import org.example.frontend.dao.ConversationDAO;
import org.example.frontend.dao.MessageDAO;
import org.example.frontend.model.Conversation;
import org.example.frontend.model.Message;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ConversationService {
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private static int conversationCounter = 0; // Be careful with this in multi-threaded apps

    public ConversationService() {
        this.conversationDAO = new ConversationDAO();
        this.messageDAO= new MessageDAO();
    }
    public List<Conversation> getAllConversations(int userID) throws SQLException {
        return this.conversationDAO.getAllConversations(userID);
    }
    public List<String> getAllTitles(int userID) {
        return this.conversationDAO.getAllConversationTitles(userID);
    }
    public int createConversation(Conversation conversation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = sdf.format(new Date(System.currentTimeMillis()));

        conversation.setDate(new Date(System.currentTimeMillis()));
        conversationCounter++;

        conversation.setTitle("New conversation at: " + formattedDate + " # " + conversationCounter);

        return this.conversationDAO.createConversation(conversation);
    }
    public void deleteConversation(int conversationID) {
        this.conversationDAO.deleteConversation(conversationID);
    }
    public int getConversationIdByTitle(String title, int userId) {
        return this.conversationDAO.getConversationIdByTitle(title,userId);
    }
}
