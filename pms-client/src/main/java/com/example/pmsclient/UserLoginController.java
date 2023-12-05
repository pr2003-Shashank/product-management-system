package com.example.pmsclient;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class UserLoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button login_btn;
    @FXML
    private Text incorrectPasswordLabel;
    @FXML
    private Text forgotPasswordLabel;

    private ClientApp clientApp;

    private double xOffset = 0;
    private double yOffset = 0;

    public UserLoginController(){
        clientApp = new ClientApp();
    }


    @FXML
    protected void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()){
            // Send the username and password to the server for authentication.
            boolean isAuthenticated = clientApp.getAuthenticationFromServer(username, password);
            // Handle the authentication result as needed.
            if (isAuthenticated) {
                System.out.println("Login successful");
                // Add code to navigate to the next scene or perform the desired action after successful login.
                incorrectPasswordLabel.setVisible(false);
                forgotPasswordLabel.setVisible(false);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));
                    Parent root = loader.load();

                    // Get the controller of the new scene if needed
                    // NewSceneController newSceneController = loader.getController();

                    // Create a new scene
                    Scene scene = new Scene(root);

                    // Get the stage information
                    Stage stage = (Stage) login_btn.getScene().getWindow();

//                stage.initStyle(StageStyle.DECORATED);

                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

                    root.setOnMousePressed(event -> {
                        xOffset = event.getScreenX() - stage.getX();
                        yOffset = event.getScreenY() - stage.getY();
                    });

                    root.setOnMouseDragged(event -> {
                        double newX = event.getScreenX() - xOffset;
                        double newY = event.getScreenY() - yOffset;

                        newX = Math.max(screenBounds.getMinX(), Math.min(newX, screenBounds.getMaxX() - stage.getWidth()));
                        newY = Math.max(screenBounds.getMinY(), Math.min(newY, screenBounds.getMaxY() - stage.getHeight()));

                        stage.setX(newX);
                        stage.setY(newY);
                    });

                    // Set the new scene on the stage
                    stage.setScene(scene);

                    stage.centerOnScreen();

                    // Optional: Set additional stage properties if needed
//                 stage.setTitle("Product Management System");
                    // stage.setResizable(false);

                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception (e.g., show an error message)
                }

            } else {
                System.out.println("Login failed");
                // Display an error message to the user.
                usernameField.setStyle("-fx-border-color: red;-fx-border-radius: 2;");
                passwordField.setStyle("-fx-border-color: red;-fx-border-radius: 2;");
                incorrectPasswordLabel.setVisible(true);
                forgotPasswordLabel.setVisible(true);
            }
        } else {
            usernameField.setStyle("-fx-border-color: red;-fx-border-radius: 2;");
            passwordField.setStyle("-fx-border-color: red;-fx-border-radius: 2;");
        }
    }



    @FXML
    protected void handleClose(){
        clientApp.stop();
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}