package nci.security_fundamentals;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
            MongoCollection<Document> collection = database.getCollection("user_data");
            // Document doc = collection.find(eq("title", "Back to the Future")).first();
            // if (doc != null) {
            //     System.out.println(doc.toJson());
            // } else {
            //     System.out.println("No matching documents found.");
            // }
        }
    }
}
