package nci.security_fundamentals.auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import nci.security_fundamentals.config.EnvConfig;
import nci.security_fundamentals.db.User_repository;

public class LoginHandler {

    MongoDatabase database;
    User_repository userRepo;
    AuthService authService;
    JwtService jwtService;

    public LoginHandler() {
        try {
            String connectionString = EnvConfig.getRequired("MONGODB_CONNECTION_STRING");
            String databaseName = EnvConfig.getRequired("MONGODB_DATABASE_NAME");
            
            MongoClient mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            userRepo = new User_repository(database);
            authService = new AuthService(userRepo);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String registerUser(String username, String password, String email) {
        try {
            return authService.registerUser(username, email, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Could not register user";
        }
    }

    public String login(String username, String password) {
        try {
            String tokenMsg = authService.login(username, password);
            if (authService.isTokenValid(tokenMsg)) {
                Path jwtPath = Paths.get(System.getProperty("user.home"));
                jwtPath = jwtPath.resolve("JSONWebToken.txt");
                
                // Delete if exists, then create
                if (Files.exists(jwtPath)) {
                    Files.delete(jwtPath);
                }
                Files.createFile(jwtPath);
                Files.writeString(jwtPath, tokenMsg);

                return tokenMsg;
            }else{
                return tokenMsg;
            }
        } catch (Exception e) {
            System.out.println("Unable to write token to file" + e);
            return e.getMessage();
        }
    }

    public boolean isSessionActive(){
        try {
            Path jwtPath = Paths.get(System.getProperty("user.home"));
            jwtPath = jwtPath.resolve("JSONWebToken.txt");
            
            //Check if the token file exists
            if (!Files.exists(jwtPath)) {
                return false;
            }
            
            //Read the token from the file
            String token = Files.readString(jwtPath);
            
            //Validate the token
            return authService.isTokenValid(token);
        } catch (Exception e) {
            System.out.println("Unable to fetch token from file: " + e);
            return false;
        }
    }


}
