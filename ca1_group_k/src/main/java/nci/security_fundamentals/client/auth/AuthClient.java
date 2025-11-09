package nci.security_fundamentals.client.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
// This class is intended to send the clients details to the auth server that will validate and return jwt
public class AuthClient {
    private final String serverIp;
    private final int port;

    public AuthClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public String login(String username, String password) {
        try {
            Socket socket = new Socket(serverIp, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.println(username);
            out.println(password);


            String response = in.readLine();
            if (response == null || response.startsWith("Incorrect") || response.startsWith("User not")) {
                return null;  // login failed
            }

            return response;

        } catch (Exception e) {
            System.out.println("Auth server unreachable: " + e.getMessage());
            return null;
        }
    }
}


