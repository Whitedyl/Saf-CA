package nci.security_fundamentals.auth;

import nci.security_fundamentals.server.db.User_repository;
import nci.security_fundamentals.server.models.User;

/**
 * AuthService - The Brain of Authentication
 * 
 * PURPOSE: Contains all authentication business logic
 * POSITION IN ARCHITECTURE: Middle layer between LoginHandler and User_repository
 * 
 * RESPONSIBILITIES:
 * - Validate login credentials
 * - Encrypt/decrypt passwords (via PasswordEncryptor)
 * - Generate JWT tokens (via JwtService)
 * - Validate JWT tokens
 * - Authenticate users by token
 * 
 * DEPENDENCIES:
 * - User_repository: To read/write user data from MongoDB
 * - JwtService: To create and validate JWT tokens
 * - PasswordEncryptor: To securely hash/verify passwords
 * 
 * WHY IT EXISTS:
 * - Separates business logic from database operations
 * - LoginHandler stays simple, AuthService handles complexity
 * - Easy to add new auth methods (OAuth, 2FA) without changing LoginHandler
 */
public class AuthService {

    // Dependencies - all auth-related services
    private final User_repository userRepository;  // Database access
    private final JwtService jwtService;          // Token operations
    PasswordEncryptor peq;                        // Password hashing

    /**
     * Constructor - Initializes all authentication services
     * 
     * @param userRepository Connected to MongoDB via LoginHandler
     */
    public AuthService(User_repository userRepository) {
        this.userRepository = userRepository;
        jwtService = new JwtService();
        peq = new PasswordEncryptor();
    }

    /**
     * Register a new user
     * 
     * PROCESS:
     * 1. Validate all fields are provided
     * 2. Encrypt password using PasswordEncryptor
     * 3. Create User object with encrypted password
     * 4. Save to database via User_repository
     * 
     * SECURITY: Password is NEVER stored in plain text
     * 
     * @return Success or failure message
     */
    public String registerUser(String username, String email, String password) {
        // Registration logic here
        try {
            if (!(username == null && email == null && password == null)) {
                // Check if username already exists
                User existingUser = userRepository.findByUsername(username);
                if (existingUser != null) {
                    return "Registration failed: Username already exists";
                }
                
                User newUser = new User(username, email, peq.encryptString(password));
                userRepository.createUser(newUser);
            } else {
                return "missing user information: email: " + email + ", username: " + username + ", password: " + password;
            }

        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }

        return "User registered successfully: " + username;
    }

    /**
     * Login user and generate JWT token
     * 
     * AUTHENTICATION FLOW:
     * 1. Look up user in database by username
     * 2. Decrypt stored password hash
     * 3. Compare with provided password
     * 4. If match: Generate JWT token with user info
     * 5. Return token (used for all future authenticated requests)
     * 
     * @return JWT token string if successful, error message if failed
     */
    public String login(String username, String password) {
        try {
            User currUser = userRepository.findByUsername(username);
            if (currUser != null) {
                String psw = currUser.getPasswordHash();
                if (peq.decryptString(psw).equals(password)) { // peq.encryptString add in later when encryption of password is finished
                    String token = jwtService.getToken(currUser);
                    return token;
                } else {
                    return "Password does not match encrypted password";
                }
            } else {
                return "user not found";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Could not log in: " + e.getMessage();
        }
    }


    /**
     * Authenticate a user using their JWT token
     * 
     * WHEN USED: Every authenticated request (chat messages, file uploads, etc.)
     * 
     * PROCESS:
     * 1. Validate token signature and expiration
     * 2. Extract username from token claims
     * 3. Look up user in database
     * 4. Return User object if valid, null if invalid
     * 
     * WHY: Allows stateless authentication - no session storage needed
     * 
     * @return User object if token is valid, null otherwise
     */
    public User authenticateWithToken(String token) {
//        String tokenResult = jwtService.validateToken(token);
//        User user = null;
//        System.out.println("[DEBUG] Token validation result: " + tokenResult);
//        if(tokenResult.equals("LockTalk")){
//            String username = jwtService.getUsernameFromToken(token);
//            System.out.println("[DEBUG] Looking for username in DB: " + username);
//            user = userRepository.findByUsername(username);
//            if (user == null) {
//                System.out.println("[DEBUG] No user found for " + username);
//            }
//            else{
//                System.out.println("[DEBUG] User found: \" "+ user.getUsername());
//            }
//        }else{
//            return user;
//        }
//
//        return user;
        System.out.println("[DEBUG] Authenticating token...");
        String tokenResult = jwtService.validateToken(token);
        System.out.println("[DEBUG] Token validation result: " + tokenResult);

        if (tokenResult.startsWith("Invalid Token:")) {
            System.out.println("[DEBUG] Token invalid. Rejecting.");
            return null;
        }

        // Try username lookup first
        String username = jwtService.getUsernameFromToken(token);
        System.out.println("[DEBUG] Extracted username from token: " + username);

        User user = userRepository.findByUsername(username);

        // If not found by username, fall back to ObjectId lookup
        if (user == null) {
            System.out.println("[DEBUG] User not found by username, trying ObjectId lookup...");
            try {
                user = userRepository.findById(new org.bson.types.ObjectId(tokenResult));
            } catch (Exception e) {
                System.out.println("[DEBUG] Could not parse ObjectId: " + e.getMessage());
            }
        }

        if (user == null) {
            System.out.println("[DEBUG] Authentication failed — user not found.");
        } else {
            System.out.println("[DEBUG] User found: " + user.getUsername());
        }

        return user;
    }

    /**
     * Quick check if a token is valid
     * 
     * CHECKS:
     * - Token signature is correct (not tampered with)
     * - Token hasn't expired
     * - Token was issued by our server
     * 
     * @return true if token is valid and can be used
     */
    public boolean isTokenValid(String token) {
        String tokenResult = jwtService.validateToken(token);
        // validateToken returns the user ID if valid, or "Invalid Token: ..." if invalid
        return !tokenResult.startsWith("Invalid Token:");
    }
    
    public String getUsernameFromToken(String token) {
        return jwtService.getUsernameFromToken(token);
    }
}
