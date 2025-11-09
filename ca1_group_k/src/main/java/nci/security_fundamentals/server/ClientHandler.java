package nci.security_fundamentals.server;

import nci.security_fundamentals.config.HMACUtils;
import nci.security_fundamentals.auth.AuthService;
import nci.security_fundamentals.server.models.User;

import java.io.*;
import java.net.Socket;

/**
 * @Author Dylan White
 * ClientHandler - Manages a single client connection
 * Runs in a seperate thread for each connected client. Handles JWT auth,
 * receives messages, verifies HMAC signature and sends messages/history to the client
 *
 * Key Methods:
 * -run(): auths client and listens for messages
 * -sendMessage() / sendHistoryMessage(): sent to specific client
 * -cleanup(): close connection and remove from server
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    private ChatServer server;
    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean isConnected;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.authService = server.getAuthService();
        this.isConnected = true;

        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to initialize streams");
            isConnected = false;
        }
    }

    @Override
    public void run() {
        try {
            // Receive JWT token from client
            String jwtToken = (String) in.readObject();

            //Verify jwt token using authservice
            if(!authService.isTokenValid(jwtToken)) {
                System.out.println("[ERROR] Invalid JWT Token");
                out.writeObject("Invalid JWT Token");
                out.flush();
                socket.close();
                return;
            }

            // Extract username from token
            this.username = authService.getUsernameFromToken(jwtToken);

            // Verify user exists in database
            User user = authService.authenticateWithToken(jwtToken);
            if (user == null) {
                System.out.println("[AUTH] User not found in database. Connection rejected.");
                out.writeObject("AUTH_FAILED");
                out.flush();
                socket.close();
                return;
            }

            // Send auth success message
            out.writeObject("AUTH_SUCCESS");
            out.flush();

            // Add client to server
            server.addClient(this);

            // Listen for messages from this client
            while (isConnected) {
                try {
                    //Receive message from client
                    String message = (String) in.readObject();

                    //Receive HMAC from client
                    String receivedHmac = (String) in.readObject();
                    //Jordan when you send a message from client you will need to send message first and then hmac so this works properly

                    if (message == null) {
                        break;
                    }

                    // Verify message integrity using HMAC
                    String fullMessage = username + ": " + message;
                    if (!HMACUtils.verifyHMAC(fullMessage, receivedHmac)) {
                        System.err.println("[SECURITY] Message from " + username + " failed HMAC verification!");
                        continue;  //skip this message, don't broadcast
                    }

                    // Broadcast to all clients
                    server.broadcastMessage(username, message);

                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("[ERROR] Invalid message format");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERROR] Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public synchronized void sendMessage(String sender, String message) {
        try {
            if (isConnected && out != null) {
                String fullMessage = sender + ": " + message;
                out.writeObject(fullMessage);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to send message to " + username);
            isConnected = false;
        }
    }

    public synchronized void sendHistoryMessage(String message) {
        try {
            if (isConnected && out != null) {
                out.writeObject("[HISTORY] " + message);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to send history to " + username);
        }
    }

    public void disconnect() {
        isConnected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Error disconnecting");
        }
    }

    private void cleanup() {
        isConnected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing resources");
        }

        server.removeClient(this);
    }

    public String getUsername() {
        return username;
    }
    public boolean isConnected() {
        return isConnected;
    }
}
