package org.example.frontend.BusinessLogic;

import org.example.frontend.dao.MessageDAO;
import org.example.frontend.model.Message;

import java.sql.Date;
import java.util.List;


public class MessageService {
    private final MessageDAO messageDAO;
    public MessageService() {
        this.messageDAO = new MessageDAO();
    }
    public void saveMessage(Message message) {
        //set the time of the message
        message.setSendAt(new Date(System.currentTimeMillis()));
        messageDAO.save(message);
    }

    public boolean messageCountIsEnough(int conversationId) {
        //yb9a
        if(messageDAO.getMessageCount(conversationId)>=2){
            return true;
        }else {//ytmsa7
            return false;
        }
    }
    public List<Message> getMessagesByConversationId(int conversationID){
        return this.messageDAO.getMessagesByConversationId(conversationID);
    }

}
