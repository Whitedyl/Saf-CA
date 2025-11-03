package nci.security_fundamentals;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
        // String uri = "mongodb+srv://andrepontde:261010@userinfo.vcyjmfx.mongodb.net/?appName=userinfo";
        // try (MongoClient mongoClient = MongoClients.create(uri)) {
        //     MongoDatabase database = mongoClient.getDatabase("LockTalk");
        //     MongoCollection<Document> collection = database.getCollection("user_data");
        //     System.out.println("Connected to the database successfully");
            // Document doc = collection.find(eq("title", "Back to the Future")).first();
            // if (doc != null) {
            //     System.out.println(doc.toJson());
            // } else {
            //     System.out.println("No matching documents found.");
            // }
            
        // }

        String connectionString = "mongodb+srv://andrepontde:261010@locktalk.llyfec8.mongodb.net/?appName=LockTalk";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
