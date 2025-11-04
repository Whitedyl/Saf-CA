package nci.security_fundamentals.models;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Message Model - Represents an encrypted message between users
 * 
 * This shows how to handle relationships in MongoDB:
 * - We store user IDs (references) instead of full user objects
 * - This is similar to foreign keys in SQL
 */
public class Message {
    
    private ObjectId id;
    private ObjectId senderId;        // Reference to User._id
    private ObjectId receiverId;      // Reference to User._id
    private String encryptedContent;  // The encrypted message content
    private String encryptionMethod;  // Which encryption was used
    private Date sentAt;
    private Date readAt;              // null if not read yet
    private boolean isDelivered;
    private boolean isRead;
    
    // Constructors
    
    public Message() {
        this.sentAt = new Date();
        this.isDelivered = false;
        this.isRead = false;
    }
    
    public Message(ObjectId senderId, ObjectId receiverId, String encryptedContent) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.encryptedContent = encryptedContent;
    }
    
    public Message(Document doc) {
        this.id = doc.getObjectId("_id");
        this.senderId = doc.getObjectId("senderId");
        this.receiverId = doc.getObjectId("receiverId");
        this.encryptedContent = doc.getString("encryptedContent");
        this.encryptionMethod = doc.getString("encryptionMethod");
        this.sentAt = doc.getDate("sentAt");
        this.readAt = doc.getDate("readAt");
        this.isDelivered = doc.getBoolean("isDelivered", false);
        this.isRead = doc.getBoolean("isRead", false);
    }
    
    public Document toDocument() {
        Document doc = new Document();
        
        if (id != null) {
            doc.append("_id", id);
        }
        
        doc.append("senderId", senderId)
           .append("receiverId", receiverId)
           .append("encryptedContent", encryptedContent)
           .append("encryptionMethod", encryptionMethod)
           .append("sentAt", sentAt)
           .append("readAt", readAt)
           .append("isDelivered", isDelivered)
           .append("isRead", isRead);
        
        return doc;
    }
    
    // Getters and Setters
    
    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public ObjectId getSenderId() {
        return senderId;
    }
    
    public void setSenderId(ObjectId senderId) {
        this.senderId = senderId;
    }
    
    public ObjectId getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(ObjectId receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getEncryptedContent() {
        return encryptedContent;
    }
    
    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }
    
    public String getEncryptionMethod() {
        return encryptionMethod;
    }
    
    public void setEncryptionMethod(String encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
    
    public Date getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
    
    public Date getReadAt() {
        return readAt;
    }
    
    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }
    
    public boolean isDelivered() {
        return isDelivered;
    }
    
    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", sentAt=" + sentAt +
                ", isRead=" + isRead +
                '}';
    }
}
