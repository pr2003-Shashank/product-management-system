package com.example.pmsclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ProductManagementController implements Initializable {

    @FXML
    private TableColumn<?, ?> pdesccolumn;
    @FXML
    private TableColumn<?, ?> pidcolumn;
    @FXML
    private TableColumn<?, ?> pnamecolumn;
    @FXML
    private TableColumn<?, ?> pricecolumn;
    @FXML
    private TableView<?> productTableView;
    @FXML
    private TableColumn<?, ?> propscolumn;
    @FXML
    private TableColumn<?, ?> suppliercolumn;
    @FXML
    private TextField pro_desc;
    @FXML
    private TextField pro_name;
    @FXML
    private TextField pro_price;
    @FXML
    private ComboBox<String> pro_supplier;
    @FXML
    private Button searchProductGo;
    @FXML
    private TextField searchProductText;
    @FXML
    private Button addProduct;

    private ClientApp clientApp;

    public ProductManagementController(){
        clientApp = new ClientApp();
    }


    @FXML
    protected void handleProductAddButtonAction(){
        String pname=pro_name.getText();
        String description=pro_desc.getText();
        String price1= pro_price.getText();
        String supplier=pro_supplier.getSelectionModel().getSelectedItem();

        // Check if any of the fields are empty
        if (!pname.isEmpty() && !description.isEmpty() && !price1.isEmpty() && !supplier.isEmpty()) {
            try {
                // Convert the price text to an integer
                int price = Integer.parseInt(price1);

                // Send product information to the server
                boolean isProductAdded = clientApp.addProductsToServer(pname, description, price, supplier);

                // Handle the product addition result as needed
//                if (isProductAdded) {
//                    System.out.println("Product added successfully");
//                    // Add code to update the UI or perform additional actions after adding the product.
//                } else {
//                    System.out.println("Failed to add the product");
//                    // Handle the case where adding the product was not successful.
//                }
                showInfoWindow(isProductAdded, "Product added successfully", "Failed to add the product");

                if (isProductAdded) {
                    // Clear the fields
                    pro_name.clear();
                    pro_desc.clear();
                    pro_price.clear();
                    pro_supplier.getSelectionModel().clearSelection();  // Clear the selection in the ComboBox
                }


            } catch (NumberFormatException e) {
//                System.out.println("Invalid price format");
                showInfoWindow(false, "Invalid price format", "Please enter a valid integer for the price");
                // Handle the case where the price is not a valid integer.
            }
        } else {
//            System.out.println("Please fill in all fields");
            showInfoWindow(false, "Incomplete Fields", "Please fill in all fields");
            // Handle the case where one or more fields are empty.
        }
    }

    private void showInfoWindow(boolean success, String successMessage, String errorMessage) {
        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(success ? "Success" : "Error");
        alert.setHeaderText(null);

        if (success) {
            alert.setContentText(successMessage);
        } else {
            alert.setContentText(errorMessage);
        }

        alert.showAndWait();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> supplierNames = clientApp.receiveSupplierNames();
//        System.out.println("Received Supplier Names:");
//        for (String sname : supplierNames) {
//            System.out.println(sname);
//        }
        pro_supplier.getItems().addAll(supplierNames);

        // Add a listener to handle ComboBox selection changes
        pro_supplier.setOnAction(event -> {
            String selectedSupplier = pro_supplier.getSelectionModel().getSelectedItem();
            System.out.println("Selected Supplier: " + selectedSupplier);
            // Add code to handle the selected supplier as needed

            // Set the selected supplier as the value of the ComboBox
            pro_supplier.setValue(selectedSupplier);
        });

        // Example: You might want to set a default value for the ComboBox
        if (!supplierNames.isEmpty()) {
            pro_supplier.getSelectionModel().select(0); // Select the first item by default
        }

    }

}

