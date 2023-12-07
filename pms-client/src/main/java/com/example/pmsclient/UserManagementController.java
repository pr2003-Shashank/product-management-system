package com.example.pmsclient;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TextField add_username, add_pass, add_email;
    @FXML
    private ComboBox<String> add_role;
    @FXML
    private Button add_btn;
    @FXML
    private TableView<List<Object>> userTableView;
    @FXML
    private TableColumn<List<Object>, Object> indexColumn;
    @FXML
    private TableColumn<List<Object>, Object> usernameColumn;
    @FXML
    private TableColumn<List<Object>, Object> roleColumn;
    @FXML
    private TableColumn<List<Object>, Object> emailColumn;
    @FXML
    private TableColumn<List<Object>, Object> dateColumn;
    @FXML
    private TableColumn<List<Object>, Void> actionsColumn;

    private ClientApp clientApp;


    public UserManagementController(){ clientApp = new ClientApp(); }

    @FXML
    protected void handleAddButtonAction() {
        String usernameToAdd = add_username.getText();
        String passwordToAdd = add_pass.getText();
        String emailToAdd = add_email.getText();
        String roleToAdd = add_role.getSelectionModel().getSelectedItem();

        if ( !usernameToAdd.isEmpty() && !passwordToAdd.isEmpty() && !emailToAdd.isEmpty() && roleToAdd != null && !roleToAdd.isEmpty()) {
            // Send all the new user details to the server to server for account creation
            boolean isUserAdded = clientApp.sendToServerForAccountCreation(usernameToAdd, passwordToAdd, emailToAdd, roleToAdd);

            // handle the result appropriately
            if (isUserAdded) {
                System.out.println("User added successfully");
                reloadUsersTableView();
                add_role.getSelectionModel().clearSelection();
                add_username.clear();
                add_pass.clear();
                add_email.clear();
            } else {
                System.out.println("User could not be added");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> roles = clientApp.getRolesFromServer();
        add_role.getItems().addAll(roles);

        populateUsersTableView();
    }

    private void populateUsersTableView() {
        // Set cell value factories for each column using Callback
        indexColumn.setCellValueFactory(cellData -> {
            int rowIndex = userTableView.getItems().indexOf(cellData.getValue()) + 1;
            return new ReadOnlyObjectWrapper<>(rowIndex);
        });
        usernameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(0)));
        roleColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(1)));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(2)));
        emailColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(3)));

//        actionsColumn.setCellFactory(createCellFactory());

        // Get your data (List<List<Object>>) and convert it to ObservableList<List<Object>>
        List<List<Object>> users = clientApp.getExistingUsersFromServer();
        ObservableList<List<Object>> usersData = FXCollections.observableArrayList(users);

        // Set the data to the TableView
        userTableView.getItems().addAll(usersData);
    }

//    private Callback<TableColumn<List<Object>, Void>, TableCell<List<Object>, Void>> createCellFactory() {
//        return new Callback<TableColumn<List<Object>, Void>, TableCell<List<Object>, Void>>() {
//            @Override
//            public TableCell<List<Object>, Void> call(TableColumn<List<Object>, Void> param) {
//                return new TableCell<List<Object>, Void>() {
//                    private final HBox hbox = new HBox();
////                    private final Button editButton = new Button();
////                    private final Button deleteButton = new Button();
//                    private ImageView editImageView = new ImageView();
//                    private ImageView deleteImageView = new ImageView();
//
//                    {
//                        // Set the edit icon
//                        Image editIcon = new Image(Objects.requireNonNull(getClass().getResource("/../../../../resources/images/edit_icon.png")).toString());
//                        editImageView = new ImageView(editIcon);
//                        editImageView.setFitWidth(10);
//                        editImageView.setFitHeight(10);
////                        editButton.setGraphic(editImageView);
////                        editButton.setText("E");
//
//                        // Set the delete icon
//                        Image deleteIcon = new Image(Objects.requireNonNull(getClass().getResource("/../../../../resources/images/edit_icon.png")).toString());
//                        deleteImageView = new ImageView(deleteIcon);
//                        deleteImageView.setFitWidth(10);
//                        deleteImageView.setFitHeight(10);
////                        deleteButton.setGraphic(deleteImageView);
////                        deleteButton.setText("D");
//
//                        // Handle edit button action
//                        editImageView.setOnMouseClicked(event -> {
//                            // Handle edit action here
//                        });
//
//                        // Handle delete button action
//                        deleteImageView.setOnMouseClicked(event -> {
//                            // Handle delete action here
//
//                        });
//
//                        // Add buttons to HBox
//                        hbox.getChildren().addAll(editImageView, deleteImageView);
//                    }
//
//
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            setGraphic(hbox);
//                        }
//                    }
//                };
//            }
//        };
//    }


    // You can call this method from elsewhere in your application
    public void reloadUsersTableView() {
        List<List<Object>> updatedUsers = clientApp.getExistingUsersFromServer();
        ObservableList<List<Object>> updatedUsersData = FXCollections.observableArrayList(updatedUsers);
        userTableView.setItems(updatedUsersData);
    }
}
