package org.example.frontend.BusinessLogic;

import org.example.frontend.dao.ConversationDAO;
import org.example.frontend.model.Conversation;

import java.sql.SQLException;
import java.util.List;

public class ConversationService {
    private final ConversationDAO conversationDAO;
    public ConversationService() {
        this.conversationDAO = new ConversationDAO();
    }
    public List<Conversation> getAllConversations(int userID) throws SQLException {
        return this.conversationDAO.getAllConversations(userID);
    }
    public List<String> getAllTitles(int userID) {
        return this.conversationDAO.getAllConversationTitles(userID);
    }
}
