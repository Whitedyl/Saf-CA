package nci.security_fundamentals;

import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import nci.security_fundamentals.db.User_repository;
import nci.security_fundamentals.models.User;

// Jwt 
// Image password 
// Encrypted messaging
// Encrypted image exchange
// File storage?
// MongoDB entire db
// Java - GUI? Datagrams encryption for messages and files
// Python login - encrypted images
public class Main {

    public static void main(String[] args) {
        // Replace the placeholder with your MongoDB deployment's connection string
        String uri = "mongodb+srv://andrepontde:261010@userinfo.vcyjmfx.mongodb.net/?appName=userinfo";
        
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("LockTalk");
            
            // Create the user repository
            User_repository userRepo = new User_repository(database);
            
            // EXAMPLE 1: Create a new user
            System.out.println("=== Creating a new user ===");
            User newUser = new User("john_doe", "john@example.com", "hashed_password_here");
            newUser.setImagePasswordHash("hashed_image_password");
            
            // Check if username already exists
            if (!userRepo.usernameExists("john_doe")) {
                User savedUser = userRepo.createUser(newUser);
                System.out.println("User created: " + savedUser);
                System.out.println("Generated ID: " + savedUser.getId());
            } else {
                System.out.println("Username already exists!");
            }
            
            // EXAMPLE 2: Find a user by username
            System.out.println("\n=== Finding user by username ===");
            User foundUser = userRepo.findByUsername("john_doe");
            if (foundUser != null) {
                System.out.println("Found user: " + foundUser);
            } else {
                System.out.println("User not found");
            }
            
            // EXAMPLE 3: Update user's last login
            System.out.println("\n=== Updating last login ===");
            if (foundUser != null) {
                boolean updated = userRepo.updateLastLogin(foundUser.getId());
                System.out.println("Last login updated: " + updated);
            }
            
            // EXAMPLE 4: Get all active users
            System.out.println("\n=== Getting all active users ===");
            List<User> activeUsers = userRepo.findActiveUsers();
            System.out.println("Active users count: " + activeUsers.size());
            for (User user : activeUsers) {
                System.out.println("  - " + user.getUsername() + " (" + user.getEmail() + ")");
            }
            
            // EXAMPLE 5: Update user information
            System.out.println("\n=== Updating user email ===");
            if (foundUser != null) {
                foundUser.setEmail("john.doe@newmail.com");
                boolean updated = userRepo.updateUser(foundUser);
                System.out.println("User updated: " + updated);
            }
            
            // EXAMPLE 6: Deactivate user (soft delete)
            System.out.println("\n=== Deactivating user ===");
            if (foundUser != null) {
                boolean deactivated = userRepo.deactivateUser(foundUser.getId());
                System.out.println("User deactivated: " + deactivated);
            }
            
            /* 
             * EXAMPLE 7: Permanently delete user (uncomment to test)
             * WARNING: This permanently removes the user from database!
             */
            // if (foundUser != null) {
            //     boolean deleted = userRepo.deleteUser(foundUser.getId());
            //     System.out.println("User deleted: " + deleted);
            // }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
