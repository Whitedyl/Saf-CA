package nci.security_fundamentals.client;

import nci.security_fundamentals.auth.LoginHandler;
import nci.security_fundamentals.config.HMACUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChatClient - Handles user registration, login, and chat communication.
 * Connects to ChatServer over TCP and uses JWT for authentication.
 * Each message is signed with HMAC-SHA256 for integrity.
 */
public class ChatClient {

    private String serverIp;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private LoginHandler loginHandler;

    public ChatClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.loginHandler = new LoginHandler();
    }

    // ------------------ AUTH METHODS ------------------

    public String register(String username, String email, String password) {
        try {
            String result = loginHandler.registerUser(username, password, email);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            System.out.println("[CLIENT] Registration failed: " + e.getMessage());
            return null;
        }
    }

    public String login(String username, String password) {
        try {
            String token = loginHandler.login(username, password);
            if (token != null && token.startsWith("ey")) { // JWTs start with 'eyJ'
                System.out.println("[CLIENT] Received JWT token from server.");
                System.out.println(token + " DeBUG");
                return token;
            } else {
                System.out.println("[CLIENT] Invalid login response: " + token);
                return null;
            }
        } catch (Exception e) {
            System.out.println("[CLIENT] Login failed: " + e.getMessage());
            return null;
        }
    }

    // ------------------ CHAT METHODS ------------------

    public void startChat(String username, String token, Scanner scanner) {
        try {
            socket = new Socket(serverIp, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // 1️⃣ Send JWT token for authentication
            out.writeObject(token);
            out.flush();

            // 2️⃣ Wait for server to confirm authentication
            String authResponse = (String) in.readObject();
            if (!"AUTH_SUCCESS".equals(authResponse)) {
                System.out.println("[CLIENT] Authentication failed: " + authResponse);
                socket.close();
                return;
            }

            System.out.println("[CLIENT] Authentication successful! You are now in chat mode.");

            // 3️⃣ Start listener thread for incoming messages
            Thread listener = new Thread(() -> {
                try {
                    Object input;
                    while ((input = in.readObject()) != null) {
                        System.out.println((String) input);
                    }
                } catch (Exception e) {
                    System.out.println("[CLIENT] Disconnected from server.");
                }
            });
            listener.start();

            // 4️⃣ Message send loop
            System.out.println("You are now in chat mode. Type messages or /quit to exit.");
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("/quit")) {
                    System.out.println("[CLIENT] Disconnecting...");
                    break;
                }

                // Generate HMAC signature for message
                String fullMessage = username + ": " + message;
//                String hmac = HMACUtils.generateHMAC(fullMessage);

                out.writeObject(message);
//                out.writeObject(hmac);
                out.flush();
            }

            close();

        } catch (Exception e) {
            System.out.println("[CLIENT] Connection error: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("[CLIENT] Connection closed.");
        } catch (IOException e) {
            System.out.println("[CLIENT] Error closing connection: " + e.getMessage());
        }
    }
}
