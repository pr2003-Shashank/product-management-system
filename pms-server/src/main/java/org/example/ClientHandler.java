package org.example;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Connection connection;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream("config.properties")) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Establish a database connection
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Input and output streams for client communication
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            String clientMessage;
            while ((clientMessage = (String) in.readObject()) != null) {
                System.out.println("Received from client: " + clientMessage);

                if (clientMessage.equals("AUTHENTICATE USER")) {
                    // Respond with user authentication - either true or false
                    String username = (String) in.readObject();
                    String password = (String) in.readObject();
                    System.out.println(username + " " + password);
                    boolean isAuthenticated = authenticateUser(username, password);
                    System.out.println(isAuthenticated);
                    out.writeObject(isAuthenticated);
                }
                else if (clientMessage.equals("ADD PRODUCT"))
                {
                    String productName = (String) in.readObject();
                    String description = (String) in.readObject();
                    int price = in.readInt();
                    String supplier = (String) in.readObject();
                    System.out.println(productName + " " + description + " " + price + " " + supplier);
                    boolean isAuthenticated = addProduct(productName, description, price, supplier);
                    System.out.println(isAuthenticated);
                    out.writeObject(isAuthenticated);

                } else if (clientMessage.equals("GET SUPPLIERS")) {
                    sendSupplierNamesToClient(out);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected : "+clientSocket.getInetAddress());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean authenticateUser(String username, String password) {
        boolean isAuthenticated = false;
        try {
            String query = "SELECT username, password FROM user WHERE username = ? and password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isAuthenticated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAuthenticated;
    }
    private boolean addProduct(String productName, String description, int price, String supplier) {
        boolean isProductAdded = false;

        try {
            // First, query the sid from suppliers table based on the supplier name
            String sidQuery = "SELECT sid FROM suppliers WHERE sname = ?";
            PreparedStatement sidStatement = connection.prepareStatement(sidQuery);
            sidStatement.setString(1, supplier);
            ResultSet sidResultSet = sidStatement.executeQuery();

            // Check if a result is found
            if (sidResultSet.next()) {
                // Extract the sid from the result set
                String sid = sidResultSet.getString("sid");

                // Now, insert the product into the products table using the obtained sid
                String productInsertQuery = "INSERT INTO products (pname, description, price, sid) VALUES (?, ?, ?, ?)";
                PreparedStatement productStatement = connection.prepareStatement(productInsertQuery);
                productStatement.setString(1, productName);
                productStatement.setString(2, description);
                productStatement.setInt(3, price);
                productStatement.setString(4, sid);

                // Execute the update, and check if any rows were affected
                int rowsAffected = productStatement.executeUpdate();

                if (rowsAffected > 0) {
                    isProductAdded = true;
                }
            } else {
                System.out.println("Supplier not found: " + supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isProductAdded;
    }

    // Method to retrieve supplier names from the database and send them to the client
    public void sendSupplierNamesToClient(ObjectOutputStream out) {
        try {
            String query = "SELECT sname FROM suppliers";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> supplierNames = new ArrayList<>();
            while (resultSet.next()) {
                String sname = resultSet.getString("sname");
                supplierNames.add(sname);
            }
            // Send the list of supplier names to the client
            out.writeObject(supplierNames);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
