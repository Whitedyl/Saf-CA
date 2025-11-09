package nci.security_fundamentals;

import nci.security_fundamentals.auth.LoginHandler;
import nci.security_fundamentals.server.ChatServer;

// Encrypted messaging
// Encrypted image exchange
// File storage?
// MongoDB entire db
// Java - GUI? Datagrams encryption for messages and files
// Python login - encrypted images
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Testing LoginHandler ===\n");
        ChatServer server = new ChatServer(8080);
        server.startServer();
        // Create LoginHandler instance
        LoginHandler loginHandler = new LoginHandler();
        
        // Test 1: Register a new user
        System.out.println("Test 1: Registering new user...");
        String registerResult = loginHandler.registerUser("testuser", "testpass123", "test@example.com");
        System.out.println("Register result: " + registerResult);
        System.out.println();
        
        // Test 2: Login with the registered user
        System.out.println("Test 2: Logging in...");
        String loginResult = loginHandler.login("testuser", "testpass123");
        System.out.println("Login result (token): " + loginResult);
        System.out.println();
        
        // Test 3: Check if session is active
        System.out.println("Test 3: Checking if session is active...");
        boolean isActive = loginHandler.isSessionActive();
        System.out.println("Session active: " + isActive);
        System.out.println();
        
        // Test 4: Try login with wrong password
        System.out.println("Test 4: Trying wrong password...");
        String wrongLoginResult = loginHandler.login("testuser", "wrongpassword");
        System.out.println("Wrong login result: " + wrongLoginResult);
        
        System.out.println("\n=== Tests Complete ===");
    }
}

