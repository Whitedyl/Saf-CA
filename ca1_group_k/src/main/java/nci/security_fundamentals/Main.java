package nci.security_fundamentals;

import nci.security_fundamentals.server.ChatServer;

// Encrypted messaging
// Encrypted image exchange
// File storage?
// MongoDB entire db
// Java - GUI? Datagrams encryption for messages and files
// Python login - encrypted images
public class Main {

    public static void main(String[] args) {
        System.out.println("=== LockTalk Server ===\n");
        ChatServer server = new ChatServer(8080);
        server.startServer();
       
    }
}

