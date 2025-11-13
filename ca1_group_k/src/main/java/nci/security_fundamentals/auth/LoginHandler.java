package nci.security_fundamentals.auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import nci.security_fundamentals.config.EnvConfig;
import nci.security_fundamentals.server.db.User_repository;

/**
 * LoginHandler - The Main Entry Point for Authentication
 * 
 * PURPOSE: High-level facade that coordinates all authentication operations
 * THINK OF IT AS: The receptionist who directs you to the right department
 * 
 * ARCHITECTURE FLOW:
 * 1. LoginHandler creates/manages the MongoDB connection
 * 2. Passes database to User_repository (database access layer)
 * 3. Passes User_repository to AuthService (business logic layer)
 * 4. AuthService uses JwtService internally for token operations
 * 
 * CONNECTION CHAIN:
 * LoginHandler → AuthService → User_repository → MongoDB
 *               ↓
 *           JwtService (via AuthService)
 * 
 * RESPONSIBILITIES:
 * - Sets up database connection on creation
 * - Exposes simple methods: registerUser(), login(), isSessionActive()
 * - Manages JWT token file persistence (saves to JSONWebToken.txt)
 * - Delegates actual auth logic to AuthService
 * 
 * WHY THIS DESIGN:
 * - Separation of concerns: UI/Main code doesn't need to know about MongoDB or JWT details
 * - Easy to test: Can mock AuthService independently
 * - Clean API: Simple methods hide complex authentication flow
 */
public class LoginHandler {

    // Core dependencies - built in constructor
    MongoDatabase database;      // MongoDB connection handle
    User_repository userRepo;    // Database access layer (CRUD operations)
    AuthService authService;     // Business logic layer (auth operations)
    JwtService jwtService;       // Token generation/validation (used by AuthService)

    /**
     * Constructor - Sets up the entire authentication system
     * 
     * WHAT HAPPENS HERE:
     * 1. Loads MongoDB credentials from .env file (via EnvConfig)
     * 2. Creates MongoDB client and connects to database
     * 3. Initializes User_repository with database connection
     * 4. Initializes AuthService with User_repository
     * 
     * This is the "startup" phase - connecting all the pieces
     */
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

    /**
     * Register a new user
     * 
     * FLOW: LoginHandler → AuthService → User_repository → MongoDB
     * - AuthService encrypts password
     * - User_repository saves to database
     * 
     * @return Success message or error description
     */
    public String registerUser(String username, String password, String email) {
        try {
            return authService.registerUser(username, email, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Could not register user";
        }
    }

    /**
     * Login user and get JWT token
     * 
     * FLOW:
     * 1. AuthService validates credentials against database
     * 2. If valid, JwtService generates a JWT token
     * 3. Token is validated
     * 4. Token is saved to JSONWebToken.txt file for session persistence
     * 
     * WHY SAVE TO FILE:
     * - Allows session to persist across app restarts
     * - Client can read token from file for authenticated requests
     * 
     * @return JWT token string if successful, error message if failed
     */
    public String login(String username, String password) {
        try {
            String tokenMsg = authService.login(username, password);
            if (authService.isTokenValid(tokenMsg)) {
                Path jwtPath = Paths.get(System.getProperty("user.dir"));
                jwtPath = jwtPath.resolve("JSONWebToken.txt");
                System.out.println(jwtPath);
                
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

    /**
     * Check if user has an active session
     * 
     * WHAT IT DOES:
     * 1. Reads JWT token from JSONWebToken.txt file
     * 2. Validates token hasn't expired and signature is valid
     * 3. Returns true if user can make authenticated requests
     * 
     * USE CASE: Check on app startup if user is still logged in
     * 
     * @return true if valid session exists, false otherwise
     */
    public boolean isSessionActive(){
        try {
            Path jwtPath = Paths.get(System.getProperty("user.dir"));
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
