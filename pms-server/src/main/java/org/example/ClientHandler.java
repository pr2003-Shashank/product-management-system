package org.example;

import java.io.*;
import java.net.Socket;
import java.sql.*;
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
}
