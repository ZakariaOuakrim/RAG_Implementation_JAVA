package org.example.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;


import org.example.frontend.BusinessLogic.ConversationService;
import org.example.frontend.BusinessLogic.MessageService;
import org.example.frontend.BusinessLogic.UserService;
import org.example.frontend.model.Conversation;
import org.example.frontend.model.Message;
import org.example.frontend.model.Sender;
import org.json.JSONObject;

public class HelloController  {

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
    private MessageService messageService;
    private UserService userService;
    private int currentUserId;
    private int currentConversationId;
    @FXML
    private VBox staticWelcomeContent;

    private HttpClient client;

    //-----------------------------------functions  -----------------------------------------------------------------------------------
    @FXML
    public void initialize() {
        //init services
        conversationService = new ConversationService();
        messageService = new MessageService();
        userService=new UserService();
        client = HttpClient.newHttpClient();


        currentUserId=this.userService.getCurrentUserID();
        //conversation jdida
        Conversation conversation = new Conversation();
        conversation.setUserID(currentUserId);
        currentConversationId=conversationService.createConversation(conversation);


        loadConversationTitles();

        //
        chatHistoryListView.setOnMouseClicked(event -> {
            String selectedConversationTitle = chatHistoryListView.getSelectionModel().getSelectedItem();
            if (selectedConversationTitle != null) {
                System.out.println("Selected conversation: " + selectedConversationTitle);
                handleConversationSelection(selectedConversationTitle);
            }
        });

        //ila clickiti 3la crtl+enter it will work too
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

    //had function hiya li kataffichi message mn mor ma tclicki 3la wa7d convo
    private void handleConversationSelection(String selectedConversationTitle) {
        try {
            chatContentVBox.getChildren().clear();

            int selectedConversationId = conversationService.getConversationIdByTitle(selectedConversationTitle, currentUserId);

            List<Message> messages = messageService.getMessagesByConversationId(selectedConversationId);
            if (messages.isEmpty()) {
                // If no messages, show welcome content
                VBox welcomeContent = createWelcomeContent();
                chatContentVBox.getChildren().add(welcomeContent);
            } else {
                for (Message message : messages) {
                    if (message.getText() != null) {
                        Label messageLabel = new Label((message.isUserMessage() ? "You: " : "EnsetBot: ") + message.getText());
                        messageLabel.setWrapText(true);
                        messageLabel.getStyleClass().add(message.isUserMessage() ? "user-message" : "bot-message");

                        chatContentVBox.getChildren().add(messageLabel);
                    } else if (message.getImage() != null) {
                        // Display image
                        byte[] imageBytes = message.getImage();
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        ImageView imageView = new ImageView(image);

                        imageView.setFitWidth(400);
                        imageView.setPreserveRatio(true);

                        Label imageLabel = new Label(message.isUserMessage() ? "You uploaded an image:" : "EnsetBot sent an image:");
                        imageLabel.getStyleClass().add(message.isUserMessage() ? "user-message" : "bot-message");

                        chatContentVBox.getChildren().add(imageLabel);
                        chatContentVBox.getChildren().add(imageView);
                    }
                }
            }
            currentConversationId = selectedConversationId;
        } catch (Exception e) {
            showAlert("Error", "Failed to load conversation: " + e.getMessage());
        }
    }


    @FXML
    private void sendMessageButtonPressed() {
        // Get the message from the input area
        String messageText = messageInputArea.getText().trim();
        Message message;

        if (messageText.isEmpty()) {
            showAlert("Input Error", "Please enter some text before sending the request.");
            return;
        }
        sendRequestToFlaskAPI(messageText);

        Label messageLabel = new Label("You: " + messageText);

        //show message fach tsifto
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("user-message");

        //zido fchatConctent
        chatContentVBox.getChildren().add(messageLabel);

        messageInputArea.clear();

        chatContentVBox.layout();
        chatContentVBox.getParent().layout();

        // Save message to database
        message = new Message();
        message.setConversationId(currentConversationId);
        message.setText(messageText);
        message.setSender(Sender.USER);
        messageService.saveMessage(message);
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

    @FXML
    private void newConversation() {
        // Create a new conversation
        Conversation conversation = new Conversation();
        conversation.setUserID(currentUserId);

        chatContentVBox.getChildren().clear();
        VBox welcomeContent = createWelcomeContent();
        chatContentVBox.getChildren().add(welcomeContent);

        currentConversationId = conversationService.createConversation(conversation);
    }



    //generate back the enset o image when clicking at new conversation
    private VBox createWelcomeContent() {
        VBox welcomeVBox = new VBox(20); // Spacing of 20 for better alignment
        welcomeVBox.setAlignment(Pos.CENTER);

        // Add padding at the bottom
        welcomeVBox.setStyle("-fx-padding: 20 0 20 0;"); // Top, Right, Bottom, Left padding

        ImageView imageView = new ImageView(new Image(getClass().getResource("/assets/ENSET-Mohammedia.png").toExternalForm()));
        imageView.setFitHeight(195);
        imageView.setFitWidth(263);
        imageView.setPreserveRatio(true);

        Label welcomeLabel = new Label("Welcome to ENSET ChatBot");
        welcomeLabel.setStyle("-fx-text-fill: #333;");
        welcomeLabel.setFont(new Font("Arial", 40));

        welcomeVBox.getChildren().addAll(imageView, welcomeLabel);

        return welcomeVBox;
    }


    //------------------------------------------Communication with RAG-----------------------------
    private void sendRequestToFlaskAPI(String userInput) {

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("input", userInput);

        String apiUrl = "http://127.0.0.1:5000/process";

        CompletableFuture<Void> responseFuture = client.sendAsync(
                HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString(), StandardCharsets.UTF_8))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).thenAccept(response -> {

            String responseBody = response.body();
            HttpHeaders headers = response.headers();
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                String apiResponse = jsonResponse.optString("response", "No response from API");
                String apiResponseImage = jsonResponse.optString("images", "No response from API");
                System.out.println(apiResponseImage);
                // check if message
                if (apiResponseImage!=null && !apiResponseImage.equals("No response from API")) {
                    Platform.runLater(() -> {
                        try {
                            String baseDirectory = "C:\\Users\\Dell\\Desktop\\ragBackEnd\\images\\";
                            String cleanedPath = apiResponseImage.replaceAll("[\\[\\]\"']", "").trim();

                            File imageFile = new File(baseDirectory + cleanedPath.replace("./images\\", ""));

                            // Create the Image object from the absolute file path
                            Image image = new Image(imageFile.toURI().toString());

                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(400);
                            imageView.setPreserveRatio(true);

                            Label imageLabel = new Label("EnsetBot sent an image:");
                            imageLabel.getStyleClass().add("bot-message");

                            chatContentVBox.getChildren().add(imageLabel);
                            chatContentVBox.getChildren().add(imageView);

                            chatContentVBox.layout();
                            chatContentVBox.getParent().layout();

                            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

                            Message message = new Message();
                            message.setConversationId(currentConversationId);
                            message.setSender(Sender.BOT);
                            message.setImage(imageBytes);
                            messageService.saveMessage(message);

                        } catch (Exception e) {
                            showAlert("Error", "Failed to load or save image: " + e.getMessage());
                        }
                    });

                } else {
                    // Handle text response
                    Platform.runLater(() -> {
                        Label messageLabel = new Label("EnsetBot: " + apiResponse);
                        messageLabel.setWrapText(true);
                        messageLabel.getStyleClass().add("bot-message");

                        chatContentVBox.getChildren().add(messageLabel);

                        chatContentVBox.layout();
                        chatContentVBox.getParent().layout();
                    });

                    Message message = new Message();
                    message.setConversationId(currentConversationId);
                    message.setSender(Sender.BOT);
                    message.setText(apiResponse);
                    messageService.saveMessage(message);
                }
            } else {
                showAlert("Error", "Failed to get a valid response from the API.");
            }
        }).exceptionally(ex -> {
            showAlert("Error", "Request failed: " + ex.getMessage());
            return null;
        });
    }

    @FXML
    public void openImage() {
        // Create a FileChooser to select an image file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(chatContentVBox.getScene().getWindow());

        if (selectedFile != null) {
            try {
                Label messageLabel = new Label("You uploaded an image:");
                messageLabel.setWrapText(true);
                messageLabel.getStyleClass().add("user-message");
                chatContentVBox.getChildren().add(messageLabel);
                String imagePath=selectedFile.toURI().toString();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(400);
                imageView.setPreserveRatio(true);
                chatContentVBox.getChildren().add(imageView);

                byte[] imageBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());

                Message message = new Message();
                message.setConversationId(currentConversationId);
                message.setImage(imageBytes);
                message.setSender(Sender.USER);
                messageService.saveMessage(message);
                //send the path of the image to the rag API and retrieve the result
                sendRequestToFlaskAPI(imagePath);

            } catch (Exception e) {
                showAlert("Error", "Failed to upload image: " + e.getMessage());
            }
        }
    }



}
