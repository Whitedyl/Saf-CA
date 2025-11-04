package nci.security_fundamentals.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import nci.security_fundamentals.models.User;

/**
 * User Repository - Handles all database operations for Users
 * 
 * This is the "Data Access Layer" - it separates database logic from business logic.
 * Think of it as a bridge between your Java objects and MongoDB.
 * 
 * CRUD Operations:
 * - Create: Insert new users
 * - Read: Find/query users
 * - Update: Modify existing users
 * - Delete: Remove users
 */
public class User_repository {
    
    private final MongoCollection<Document> collection;
    
    /**
     * Constructor - takes a MongoDB database connection
     * @param database The MongoDB database instance
     */
    public User_repository(MongoDatabase database) {
        // Get the "users" collection (like a table in SQL)
        // If it doesn't exist, MongoDB creates it automatically
        this.collection = database.getCollection("user_data");
    }
    
    // CREATE Operations
    
    /**
     * Create a new user in the database
     * @param user The user object to save
     * @return The saved user with generated ID
     */
    public User createUser(User user) {
        // Convert User object to MongoDB Document
        Document doc = user.toDocument();
        
        // Insert into MongoDB
        collection.insertOne(doc);
        
        // MongoDB automatically adds an _id field
        // Get it and set it in our User object
        user.setId(doc.getObjectId("_id"));
        
        return user;
    }
    
    // READ Operations
    
    /**
     * Find a user by their ID
     * @param id The MongoDB ObjectId
     * @return User object or null if not found
     */
    public User findById(ObjectId id) {
        // Filters.eq creates a query: { "_id": id }
        Document doc = collection.find(Filters.eq("_id", id)).first();
        
        // If found, convert Document to User object
        return doc != null ? new User(doc) : null;
    }
    
    /**
     * Find a user by username
     * @param username The username to search for
     * @return User object or null if not found
     */
    public User findByUsername(String username) {
        // Query: { "username": username }
        Document doc = collection.find(Filters.eq("username", username)).first();
        return doc != null ? new User(doc) : null;
    }
    
    /**
     * Find a user by email
     * @param email The email to search for
     * @return User object or null if not found
     */
    public User findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? new User(doc) : null;
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        // Iterate through all documents in the collection
        for (Document doc : collection.find()) {
            users.add(new User(doc));
        }
        
        return users;
    }
    
    /**
     * Get all active users
     * @return List of active users
     */
    public List<User> findActiveUsers() {
        List<User> users = new ArrayList<>();
        
        // Query: { "isActive": true }
        for (Document doc : collection.find(Filters.eq("isActive", true))) {
            users.add(new User(doc));
        }
        
        return users;
    }
    
    // UPDATE Operations
    
    /**
     * Update an existing user
     * @param user The user with updated information
     * @return true if update was successful
     */
    public boolean updateUser(User user) {
        if (user.getId() == null) {
            return false; // Can't update without an ID
        }
        
        // Create update document with all fields
        UpdateResult result = collection.updateOne(
            Filters.eq("_id", user.getId()),
            Updates.combine(
                Updates.set("username", user.getUsername()),
                Updates.set("email", user.getEmail()),
                Updates.set("passwordHash", user.getPasswordHash()),
                Updates.set("imagePasswordHash", user.getImagePasswordHash()),
                Updates.set("lastLogin", user.getLastLogin()),
                Updates.set("isActive", user.isActive())
            )
        );
        
        return result.getModifiedCount() > 0;
    }
    
    /**
     * Update user's last login time
     * @param userId The user's ID
     * @return true if successful
     */
    public boolean updateLastLogin(ObjectId userId) {
        UpdateResult result = collection.updateOne(
            Filters.eq("_id", userId),
            Updates.set("lastLogin", new Date())
        );
        
        return result.getModifiedCount() > 0;
    }
    
    /**
     * Deactivate a user (soft delete)
     * @param userId The user's ID
     * @return true if successful
     */
    public boolean deactivateUser(ObjectId userId) {
        UpdateResult result = collection.updateOne(
            Filters.eq("_id", userId),
            Updates.set("isActive", false)
        );
        
        return result.getModifiedCount() > 0;
    }
    
    // DELETE Operations
    
    /**
     * Permanently delete a user (hard delete)
     * @param userId The user's ID
     * @return true if successful
     */
    public boolean deleteUser(ObjectId userId) {
        DeleteResult result = collection.deleteOne(Filters.eq("_id", userId));
        return result.getDeletedCount() > 0;
    }
    
    /**
     * Check if username already exists
     * @param username The username to check
     * @return true if exists
     */
    public boolean usernameExists(String username) {
        return collection.countDocuments(Filters.eq("username", username)) > 0;
    }
    
    /**
     * Check if email already exists
     * @param email The email to check
     * @return true if exists
     */
    public boolean emailExists(String email) {
        return collection.countDocuments(Filters.eq("email", email)) > 0;
    }
}
