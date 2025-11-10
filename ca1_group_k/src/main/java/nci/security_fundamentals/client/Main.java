package nci.security_fundamentals.client;



package nci.security_fundamentals.client;

import nci.security_fundamentals.client.ChatClient;
import java.util.Scanner;

public class Main {

    public static final String SERVER_IP = "localhost";
    public static final int PORT = 8080;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Secure Chat Client ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("> ");

        int choice = Integer.parseInt(scanner.nextLine());
        ChatClient client = new ChatClient(SERVER_IP, PORT);

        try {

            if (choice == 1) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Email: ");
                String email = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                String result = client.register(username, email, password);
                System.out.println(result);
                return;
            }

            if (choice == 2) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                String token = client.login(username, password);
                if (token == null) {
                    System.out.println("Login failed.");
                    return;
                }

                System.out.println("Login successful.");
                client.enterChat(username, token);
                client.startListener();
                System.out.println("You are now in chat mode. Type messages or /quit to exit.");

                while (true) {
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("/quit")) {
                        client.close();
                        break;
                    }
                    client.send(message);
                }
            }

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}


}
