package org.example.frontend.BusinessLogic;

import org.example.frontend.dao.MessageDAO;
import org.example.frontend.model.Message;

public class MessageService {
    private final MessageDAO messageDAO;
    public MessageService() {
        this.messageDAO = new MessageDAO();
    }
    public MessageDAO sendMessage(Message message) {
        
    }
}
