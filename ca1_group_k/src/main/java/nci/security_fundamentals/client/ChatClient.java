package nci.security_fundamentals.client;

import nci.security_fundamentals.auth.LoginHandler;
import nci.security_fundamentals.config.EnvConfig;
import nci.security_fundamentals.config.HMACUtils;
import nci.security_fundamentals.security.AesUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChatClient - Handles user registration, login, and chat communication.
 * Connects to ChatServer over TCP and uses JWT for authentication.
 * Each message is signed with HMAC-SHA256 for integrity.
 */
public class ChatClient {
    private SecretKey aesKey;
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

            // send jwt token for auth
            out.writeObject(token);
            out.flush();

            //⃣ Wait for server to confirm authentication
            String authResponse = (String) in.readObject();
            if (!"AUTH_SUCCESS".equals(authResponse)) {
                System.out.println("[CLIENT] Authentication failed: " + authResponse);
                socket.close();
                return;
            }
            try {

                String base64Key = EnvConfig.getRequired("AES_SECRET_KEY");


                byte[] decodedKey = Base64.getDecoder().decode(base64Key);


                aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


                System.out.println("[DEBUG] AES key length (bytes): " + aesKey.getEncoded().length);
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to load AES key: " + e.getMessage());
                return; // stop if key fails to load
            }
            // successful auth
            System.out.println("[CLIENT] Authentication successful! You are now in chat mode.");

            // Start listener thread for incoming messages
            try {
                Thread listener = new Thread(() -> {
                    try {
                        Object input;
                        while ((input = in.readObject()) != null) {
                            String encrypted = (String) input;
                            try {
                                if (encrypted.startsWith("[SERVER]")) {
                                    System.out.println(encrypted);
                                } else {
                                    try {
                                        // Split the incoming message at the first colon
                                        String[] parts = encrypted.split(":", 2);
                                        if (parts.length == 2) {
                                            String sender = parts[0].trim();
                                            String cipherText = parts[1].trim();
                                            String decrypted = AesUtils.decrypt(aesKey, cipherText);
                                            System.out.println("[" + sender + "] " + decrypted);
                                        } else {
                                            System.out.println(encrypted);
                                        }
                                    } catch (Exception ex) {
                                        System.out.println(encrypted);
                                    }

                                }
                            } catch (Exception ex) {
                                System.out.println(encrypted);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[CLIENT] Disconnected from server.");
                    }
                });
                listener.start();
            } catch (Exception e) {
                System.out.println("[CLIENT] Listener error: " + e.getMessage());
            }


            // message send loop
            System.out.println("You are now in chat mode. Type messages or /quit to exit.");
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("/quit")) {
                    System.out.println("[CLIENT] Disconnecting...");
                    break;
                }

                try {
                    // Encrypt the message before sending
                    String encrypted = AesUtils.encrypt(aesKey, message);
                    // Generate HMAC after encryption
                    String hmac = HMACUtils.generateHMAC(username + ": " + encrypted);

                    out.writeObject(encrypted);
                    out.writeObject(hmac);
                    out.flush();
                } catch (Exception e) {
                    System.out.println("[CLIENT] Encryption failed: " + e.getMessage());
                }
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

