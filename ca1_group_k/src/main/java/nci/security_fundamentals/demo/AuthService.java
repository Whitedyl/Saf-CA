package nci.security_fundamentals.demo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;

import nci.security_fundamentals.db.User_repository;
import nci.security_fundamentals.models.User;

/**
 * Authentication Service - DEMO VERSION
 * Handles user authentication and authorization
 */
public class AuthService {
    
    private final User_repository userRepository;
    private final JwtService jwtService;
    
    public AuthService(User_repository userRepository) {
        this.userRepository = userRepository;
        this.jwtService = new JwtService();
    }
    
    public User register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Registration failed: Username cannot be empty");
            return null;
        }
        
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Registration failed: Email cannot be empty");
            return null;
        }
        
        if (password == null || password.length() < 6) {
            System.err.println("Registration failed: Password must be at least 6 characters");
            return null;
        }
        
        if (userRepository.usernameExists(username)) {
            System.err.println("Registration failed: Username already exists");
            return null;
        }
        
        if (userRepository.emailExists(email)) {
            System.err.println("Registration failed: Email already exists");
            return null;
        }
        
        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            System.err.println("Registration failed: Could not hash password");
            return null;
        }
        
        User newUser = new User(username, email, passwordHash);
        User savedUser = userRepository.createUser(newUser);
        
        System.out.println("User registered successfully: " + username);
        return savedUser;
    }
    
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            System.err.println("Login failed: User not found");
            return null;
        }
        
        if (!user.isActive()) {
            System.err.println("Login failed: Account is deactivated");
            return null;
        }
        
        String passwordHash = hashPassword(password);
        if (passwordHash == null || !passwordHash.equals(user.getPasswordHash())) {
            System.err.println("Login failed: Invalid password");
            return null;
        }
        
        userRepository.updateLastLogin(user.getId());
        String token = jwtService.generateToken(user);
        
        System.out.println("Login successful: " + username);
        return token;
    }
    
    public User authenticateWithToken(String token) {
        if (!jwtService.validateToken(token)) {
            System.err.println("Authentication failed: Invalid or expired token");
            return null;
        }
        
        String userIdString = jwtService.getUserIdFromToken(token);
        if (userIdString == null) {
            System.err.println("Authentication failed: Could not extract user ID from token");
            return null;
        }
        
        ObjectId userId = new ObjectId(userIdString);
        User user = userRepository.findById(userId);
        
        if (user == null) {
            System.err.println("Authentication failed: User not found");
            return null;
        }
        
        if (!user.isActive()) {
            System.err.println("Authentication failed: Account is deactivated");
            return null;
        }
        
        return user;
    }
    
    public boolean changePassword(ObjectId userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            System.err.println("Password change failed: User not found");
            return false;
        }
        
        String oldPasswordHash = hashPassword(oldPassword);
        if (oldPasswordHash == null || !oldPasswordHash.equals(user.getPasswordHash())) {
            System.err.println("Password change failed: Current password is incorrect");
            return false;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            System.err.println("Password change failed: New password must be at least 6 characters");
            return false;
        }
        
        String newPasswordHash = hashPassword(newPassword);
        if (newPasswordHash == null) {
            System.err.println("Password change failed: Could not hash new password");
            return false;
        }
        
        user.setPasswordHash(newPasswordHash);
        boolean updated = userRepository.updateUser(user);
        
        if (updated) {
            System.out.println("Password changed successfully");
        }
        
        return updated;
    }
    
    public boolean isTokenValid(String token) {
        return jwtService.validateToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtService.getUsernameFromToken(token);
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    public void printTokenInfo(String token) {
        jwtService.printTokenInfo(token);
    }
}
