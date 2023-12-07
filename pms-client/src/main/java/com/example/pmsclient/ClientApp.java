package com.example.pmsclient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class ClientApp extends Application {

    private static final String SERVER_HOST = "localhost"; // Replace with your server's hostname or IP address
    private static final int SERVER_PORT = 12345; // Replace with your server's port

    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user-login.fxml")));

        primaryStage.initStyle(StageStyle.UNDECORATED);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        root.setOnMousePressed(event -> {
            xOffset = event.getScreenX() - primaryStage.getX();
            yOffset = event.getScreenY() - primaryStage.getY();
        });

        root.setOnMouseDragged(event -> {
            double newX = event.getScreenX() - xOffset;
            double newY = event.getScreenY() - yOffset;

            newX = Math.max(screenBounds.getMinX(), Math.min(newX, screenBounds.getMaxX() - primaryStage.getWidth()));
            newY = Math.max(screenBounds.getMinY(), Math.min(newY, screenBounds.getMaxY() - primaryStage.getHeight()));

            primaryStage.setX(newX);
            primaryStage.setY(newY);
        });

        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();

        establishConnectionWithServer();
    }

    public void establishConnectionWithServer() {
        try {
            // Establish a connection to the server.
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.err.println("Server Offline!!!");
        }
    }

    public boolean getAuthenticationFromServer(String username, String password) {
        boolean isAuthenticated = false;
        try {

            out.writeObject("AUTHENTICATE USER");
            // Send username and password to the server for authentication.
            out.writeObject(username);
            out.writeObject(password);


            // Receive the server's response.
            isAuthenticated = (boolean) in.readObject();
            System.out.println(isAuthenticated);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return isAuthenticated;
    }

    public List<String> receiveSupplierNames() {
        List<String> supplierNames = new ArrayList<>();

        try {
            // Assuming the server sends a List<String> of supplier names
            out.writeObject("GET SUPPLIERS");
            supplierNames = (List<String>) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return supplierNames;
    }


    public boolean addProductsToServer(String pname, String description, int price, String suppliers) {
        boolean isProductAdded = false;
        try {
            // Assuming out is the ObjectOutputStream and in is the ObjectInputStream
            out.writeObject("ADD PRODUCT");

            // Send product information to the server.
            out.writeObject(pname);
            out.writeObject(description);
            out.writeInt(price);
            out.writeObject(suppliers);

            // Receive the server's response.
            isProductAdded = (boolean) in.readObject();
            System.out.println("Product added: " + isProductAdded);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return isProductAdded;
    }



    @Override
    public void stop() {
        try {
//            if (out != null) {
//                out.close();
//            }
//            if (in != null) {
//                in.close();
//            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
