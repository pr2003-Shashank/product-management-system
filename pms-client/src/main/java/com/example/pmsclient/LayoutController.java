package com.example.pmsclient;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LayoutController implements Initializable {
    @FXML
    private Button close;
    @FXML
    private Button minimize;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private AnchorPane pane1, pane2;
    @FXML
    private Button menu;

    private boolean isPane2Open = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        close.setOnAction(event -> {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.close();
        });
        minimize.setOnAction(event -> {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        });

        pane1.setVisible(false);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5),pane1);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),pane2);
        translateTransition.setByX(-600);
        translateTransition.play();



        menu.setOnAction(event -> {
            if (!isPane2Open) {
                openPane2();
            } else {
                closePane2();
            }
        });

        pane1.setOnMouseClicked(event -> {
            if (isPane2Open) {
                closePane2();
            }
        });

        try{
            Parent content = FXMLLoader.load(getClass().getResource("user-management.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            Logger.getLogger(LayoutController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void openPane2() {
        pane1.setVisible(true);

        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
        fadeTransition1.setFromValue(0);
        fadeTransition1.setToValue(0.15);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
        translateTransition1.setByX(+600);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition1, translateTransition1);
        parallelTransition.play();

        isPane2Open = true;
    }

    private void closePane2() {
        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
        fadeTransition1.setFromValue(0.15);
        fadeTransition1.setToValue(0);

        fadeTransition1.setOnFinished(event1 -> pane1.setVisible(false));

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
        translateTransition1.setByX(-600); // Adjust this value based on your layout

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition1, translateTransition1);
        parallelTransition.play();

        isPane2Open = false;
    }
}
