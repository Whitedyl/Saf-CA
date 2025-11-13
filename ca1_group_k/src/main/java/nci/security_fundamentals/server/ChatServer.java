package nci.security_fundamentals.server;

import nci.security_fundamentals.auth.AuthService;
import nci.security_fundamentals.config.EnvConfig;
import nci.security_fundamentals.server.db.User_repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Dylan White
 * Chat Server - multithreaded chat server with message broadcasting
 *
 * Listens for incoming clients on port 8080 and creates a ClientHandler thread for each connected client
 * broadcasts messages to all client and maintains chat history.
 *
 * Key Methods:
 * -startServer(): Listens for incoming clients and creates ClientHandler threads
 * -broadcastMessage(): Sends messages to all connected clients
 * -addClient() / removeClient(): manages client connections
 * -sendChatHistory(): sends messages end before a new client joins
 */
public class ChatServer {
    private int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private List<ClientHandler> connectedClients;
    private List<String> messageHistory;
    private AuthService authService;
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_CLIENTS = 10;


    public ChatServer(int port) {

        try {
            this.port = port;
            this.isRunning = false;
            this.connectedClients = new CopyOnWriteArrayList<>();
            this.messageHistory = new CopyOnWriteArrayList<>();

            // Get connection string and database name from .env file
            String connectionString = EnvConfig.getRequired("MONGODB_CONNECTION_STRING");
            String databaseName = EnvConfig.getRequired("MONGODB_DATABASE_NAME");

            // Create MongoDB connection
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            // Create userRepository with database
            User_repository userRepository = new User_repository(database);

            // Create AuthService with the repository
            this.authService = new AuthService(userRepository);

            System.out.println("[SERVER] ChatServer initialized successfully");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to initialize ChatServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ChatServer server = new ChatServer(DEFAULT_PORT);
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("[SERVER] Server started on port " + port);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Accepted connection from " + clientSocket.getInetAddress().getHostAddress());

                //Starting a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch(IOException e) {
            System.out.println("[ERROR] Server start failed on port " + port);
        }
    }

    public void stopServer() {
        isRunning = false;

        // Disconnect all clients
        for (ClientHandler client : connectedClients) {
            client.disconnect();
        }
        connectedClients.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing server socket");
        }

        // Clear message history
        messageHistory.clear();
        System.out.println("[SERVER] Message history cleared");
        System.out.println("[SERVER] Server stopped");
    }

    public synchronized void addClient(ClientHandler client) {
        if (connectedClients.size() < MAX_CLIENTS) {
            connectedClients.add(client);
            System.out.println("[AUTH] " + client.getUsername() + " authenticated and joined chat");

            // Send chat history to new client
            sendChatHistory(client);
        } else if (connectedClients.size() > MAX_CLIENTS) {
            System.out.println("[ERROR] Too many connections");
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        if (connectedClients.remove(client)) {
            System.out.println("[DISCONNECT] " + client.getUsername() + " left. Users online: " + connectedClients.size());
        }
    }


    public void broadcastMessage(String sender, String message) {
        String fullMessage = sender + ": " + message;
        messageHistory.add(fullMessage);  // Store in ArrayList

        System.out.println("[MSG] Received from " + sender + ": \"" + message + "\"");
        System.out.println("[FILE] Message saved");
        System.out.println("[BROADCAST] Sent to " + connectedClients.size() + " users");

        for (ClientHandler client : connectedClients) {
            client.sendMessage(sender, message);
        }
    }

    public void sendChatHistory(ClientHandler newClient) {
        if (messageHistory.isEmpty()) {
            System.out.println("[HISTORY] No previous messages for " + newClient.getUsername());
            return;
        }

        System.out.println("[HISTORY] Sending " + messageHistory.size() + " previous messages to " + newClient.getUsername());
        for (String message : messageHistory) {
            newClient.sendHistoryMessage(message);
        }
    }

    public List<ClientHandler> getConnectedClients() {
        return new ArrayList<>(connectedClients);
    }

    public List<String> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }
    public AuthService getAuthService() {
        return authService;
    }

}
