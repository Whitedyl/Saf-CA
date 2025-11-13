package nci.security_fundamentals.client;



/**
 * ClientApp - Entry point for the Secure Chat Client.
 * Handles registration, login, and starting chat sessions.
 */


import java.util.Scanner;

public class Main {

    public static final String SERVER_IP = "localhost";
    public static final int PORT = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatClient client = new ChatClient(SERVER_IP, PORT);

        System.out.println("=== Secure Chat Client ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("> ");
        int choice = Integer.parseInt(scanner.nextLine());

        try {
            String token = null;
            String username = null;

            if (choice == 1) {
                System.out.print("Username: ");
                username = scanner.nextLine();

                System.out.print("Email: ");
                String email = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                String result = client.register(username, email, password);
                System.out.println(result);

                // Only auto-login if registration succeeded
                if (result.toLowerCase().contains("success")) {
                    token = client.login(username, password);
                    if (token == null) {
                        System.out.println("Login failed after registration. Please try manually.");
                        return;
                    }
                } else {
                    return; // registration failed
                }
            }

            if (choice == 2) {
                System.out.print("Username: ");
                username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                token = client.login(username, password);
                if (token == null) {
                    System.out.println("Login failed. Please check credentials.");
                    return;
                }
            }

            // Only connect to chat if JWT token is valid
            if (token != null && token.startsWith("ey")) {
                client.startChat(username, token, scanner);
            } else {
                System.out.println("Invalid JWT token, cannot start chat.");
            }

        } catch (Exception e) {
            System.out.println("[CLIENT] Error: " + e.getMessage());
        }
    }
}

