package org.example;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
                } else if (clientMessage.equals("GET ROLES")) {
                    // Send the list of roles to client
                    List<String> roles = new ArrayList<>();
                    roles = getRoles();
                    System.out.println(roles);
                    out.writeObject(roles);
                } else if (clientMessage.equals("ADD USER")) {
                    // Respond with true if creation successfull false otherwise
                    String username = (String) in.readObject();
                    String password = (String) in.readObject();
                    String email = (String) in.readObject();
                    String role = (String) in.readObject();
                    System.out.println(username+" "+password+" "+email+" "+role);
                    boolean isUserAdded = addUser(username, password, email, role);
                    System.out.println(isUserAdded);
                    out.writeObject(isUserAdded);
                } else if (clientMessage.equals("GET USER TABLE")) {
                    // Send the list of users with all details to client
                    List<List<Object>> users = new ArrayList<>();
                    users = getExistingUsers();
                    System.out.println(users);
                    out.writeObject(users);
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

    private boolean addUser(String username, String password, String email, String role) {
        boolean isUserAdded = false;
        try {
            String query = "INSERT INTO user (username, password, email, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, role);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                isUserAdded = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isUserAdded;
    }

    private List<String> getRoles() {
        List<String> roles = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT role_name FROM role";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String role = resultSet.getString("role_name");
                    roles.add(role);
                }
            }

        } catch (SQLException e) {
            // Handle the exception according to your needs
            throw new RuntimeException("Error retrieving roles from the database", e);
        }
        return roles;
    }

    private List<List<Object>> getExistingUsers() {
        List<List<Object>> users = new ArrayList<>();

        try {
            String query = "SELECT username, role, DATE(date_created) as date_created, email FROM user";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String role = resultSet.getString("role");
                    String date_created = resultSet.getString("date_created");

                    // Create a list for each user and add all information to that list
                    List<Object> userInformation = new ArrayList<>();
                    userInformation.add(username);
                    userInformation.add(role);
                    userInformation.add(date_created);
                    userInformation.add(email);

                    // Add the user information list to the main list
                    users.add(userInformation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users from database",e);
        }
        return users;
    }
}
