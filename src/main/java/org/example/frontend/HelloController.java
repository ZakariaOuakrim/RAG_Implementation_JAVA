package org.example.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.example.frontend.BusinessLogic.ConversationService;

public class HelloController {

    @FXML
    public VBox chatContentVBox;

    @FXML
    private ListView<String> chatHistoryListView;

    @FXML
    private TextArea currentConversationTextArea;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea messageInputArea;

    private ObservableList<String> conversations;
    private ConversationService conversationService;
    private int currentUserId;

    @FXML
    public void initialize() {

        currentUserId=1;
        conversationService = new ConversationService();
        loadConversationTitles();
        // Add key listener for Ctrl + Enter to send message
        messageInputArea.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    if (event.isControlDown()) {
                        sendMessageButtonPressed();
                        event.consume();
                    }
                    break;
                default:
                    break;
            }
        });
    }
    @FXML
    private void sendMessageButtonPressed() {
        // Get the message from the input area
        String message = messageInputArea.getText().trim();

        // Check if the message is not empty
        if (!message.isEmpty()) {
            // Create a new TextArea to display the message
            TextArea messageTextArea = new TextArea("You: " + message);

            // Set properties for the new TextArea
            messageTextArea.setEditable(false); // Make it read-only
            messageTextArea.setWrapText(true); // Enable text wrapping
            messageTextArea.getStyleClass().add("user-message"); // Optional: Add CSS class for styling

            // Add the new TextArea to the chatContentVBox
            chatContentVBox.getChildren().add(messageTextArea);

            // Clear the input field
            messageInputArea.clear();

            // Optionally, scroll to the bottom of the ScrollPane to show the latest message
            chatContentVBox.layout(); // Refresh layout
            chatContentVBox.getParent().layout(); // Ensure parent is updated
        }
    }
    //function katloadi conversation titles
    private void loadConversationTitles() {
        try{
            conversations=FXCollections.observableArrayList(conversationService.getAllTitles(currentUserId));
            chatHistoryListView.setItems(conversations);
        }catch (Exception e){
            showAlert("Error", "Failed to conversations: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
