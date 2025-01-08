package org.example.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.frontend.BusinessLogic.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class Authentification {
    public TextField FirstName;
    public TextField LastName;
    public TextField Email;
    public TextField Password;
    public TextField ConfirmPassword;
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Label label ;

    @FXML
    Label labelSignUp;

    private UserService userService;

    @FXML
    public void initialize() {
        this.userService=new UserService();

    }

    public void login(ActionEvent event) throws SQLException {

        if (!(username.getText().equals("")) && !(password.getText().equals("")))
        {
            int isauth = userService.login(username.getText(), password.getText());
            if(isauth!=-1){
                label.setText("Login Successful");
                try {
                    ((Node)event.getSource()).getScene().getWindow().hide();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("chatBot.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.setTitle("Chat Bot");
                    stage.setScene(scene);
                    stage.show();
                } catch(Exception e) {
                    e.printStackTrace();
                }


            }
            else{
                // add label
                label.setText("Login Not Successful");
                label.setStyle("-fx-text-fill: red");
            }
        }
        else{
            // add label
            label.setText("Some field is empty");
            label.setStyle("-fx-text-fill: red");
        }

    }

    public void createNewAccount(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setTitle("Sign Up");
        stage.setScene(scene);
        stage.show();



        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    public void signUp(ActionEvent event) throws SQLException {
        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        String email = Email.getText();
        String passwordText = Password.getText();
        String confirmPasswordText = ConfirmPassword.getText();

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            labelSignUp.setText("Please fill all the fields.");
            labelSignUp.setStyle("-fx-text-fill: red");
            return;
        }

        // Check if passwords match
        if (!passwordText.equals(confirmPasswordText)) {
            labelSignUp.setText("Passwords do not match.");
            labelSignUp.setStyle("-fx-text-fill: red");
            return;
        }

        // Check if the user already exists
        if (userService.checkIfUserExists(email)) {
            labelSignUp.setText("User with this email already exists.");
            labelSignUp.setStyle("-fx-text-fill: red");
            return;
        }

        try {
            userService.signUp(firstName, lastName, email, passwordText);
            labelSignUp.setText("Sign up successful! You can now log in.");
            labelSignUp.setStyle("-fx-text-fill: green");
            //redirectini l'login page
            redirectToLoginPage(event);


        } catch (SQLException e) {
            labelSignUp.setText("Error occurred during sign up. Please try again.");
            labelSignUp.setStyle("-fx-text-fill: red");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void redirectToLoginPage(ActionEvent event) throws IOException {
        // Load the login page FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        // Get the current stage (window) and hide it
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();

        // Create a new scene for the login page
        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);

        // Show the login page
        stage.show();
    }
}
