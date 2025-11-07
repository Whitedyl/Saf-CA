package nci.security_fundamentals.server.models;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * User Model - Represents a user in the LockTalk system
 * 
 * In MongoDB with Java, we don't have strict schemas like SQL databases.
 * Instead, we create POJO (Plain Old Java Object) classes that represent
 * our document structure and provide methods to convert to/from MongoDB Documents.
 * 
 * This is the "schema" - it defines what fields a User should have.
 */
public class User {
    
    // Fields representing the user schema
    private ObjectId id;              // MongoDB's unique identifier (_id field)
    private String username;          // Unique username
    private String email;             // User's email
    private String passwordHash;      // NEVER store plain passwords! Store hashed passwords
    private String imagePasswordHash; // Hash of the image password
    private Date createdAt;           // When the user was created
    private Date lastLogin;           // Last login timestamp
    private boolean isActive;         // Account status
    
    // Constructors
    
    /**
     * Default constructor - required for creating new instances
     */
    public User() {
        this.createdAt = new Date();
        this.isActive = true;
    }
    
    /**
     * Constructor with essential fields
     */
    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    /**
     * Constructor from MongoDB Document
     * This is how we convert a MongoDB document back into a Java object
     */
    public User(Document doc) {
        this.id = doc.getObjectId("_id");
        this.username = doc.getString("username");
        this.email = doc.getString("email");
        this.passwordHash = doc.getString("passwordHash");
        this.imagePasswordHash = doc.getString("imagePasswordHash");
        this.createdAt = doc.getDate("createdAt");
        this.lastLogin = doc.getDate("lastLogin");
        this.isActive = doc.getBoolean("isActive", true);
    }
    
    // Conversion Methods
    
    /**
     * Convert this User object to a MongoDB Document
     * This is how we save the object to MongoDB
     */
    public Document toDocument() {
        Document doc = new Document();
        
        // Only include _id if it exists (for updates)
        if (id != null) {
            doc.append("_id", id);
        }
        
        doc.append("username", username)
           .append("email", email)
           .append("passwordHash", passwordHash)
           .append("imagePasswordHash", imagePasswordHash)
           .append("createdAt", createdAt)
           .append("lastLogin", lastLogin)
           .append("isActive", isActive);
        
        return doc;
    }
    
    // Getters and Setters
    // These allow controlled access to private fields
    
    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getImagePasswordHash() {
        return imagePasswordHash;
    }
    
    public void setImagePasswordHash(String imagePasswordHash) {
        this.imagePasswordHash = imagePasswordHash;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Utility Methods
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
