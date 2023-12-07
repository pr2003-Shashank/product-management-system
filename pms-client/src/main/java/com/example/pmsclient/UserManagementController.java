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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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




        // Get your data (List<List<Object>>) and convert it to ObservableList<List<Object>>
        List<List<Object>> users = clientApp.getExistingUsersFromServer();
        ObservableList<List<Object>> usersData = FXCollections.observableArrayList(users);

        // Set the data to the TableView
        userTableView.getItems().addAll(usersData);

    }


    // You can call this method from elsewhere in your application
    public void reloadUsersTableView() {
        List<List<Object>> updatedUsers = clientApp.getExistingUsersFromServer();
        ObservableList<List<Object>> updatedUsersData = FXCollections.observableArrayList(updatedUsers);
        userTableView.setItems(updatedUsersData);
    }

    protected void handleEditButtonAction() {

    }

    protected void handleDeleteButtonAction() {

    }
}
