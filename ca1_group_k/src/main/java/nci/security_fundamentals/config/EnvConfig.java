package nci.security_fundamentals.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * EnvConfig - Configuration Manager
 * 
 * PURPOSE: Single source of truth for all environment variables (like secrets, API keys, DB connections)
 * WHY: Keeps sensitive data OUT of code and IN .env files (never commit .env to git!)
 * 
 * HOW IT FITS:
 * - Used by LoginHandler to get MongoDB connection string
 * - Used by JwtService to get JWT secret key for token signing
 * - Any class needing config calls EnvConfig.get() or EnvConfig.getRequired()
 * 
 * USAGE:
 *   String dbUrl = EnvConfig.getRequired("MONGODB_CONNECTION_STRING");
 *   String apiKey = EnvConfig.get("OPTIONAL_KEY"); // returns null if not found
 */
public class EnvConfig {
    
    private static Dotenv dotenv;
    
    static {
        loadEnvironment();
    }
    
    /**
     * Loads the .env file from the first available location.
     * Tries: current directory -> ca1_group_k subdirectory -> parent directory
     */
    private static void loadEnvironment() {
        if (new java.io.File(".env").exists()) {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } else if (new java.io.File("ca1_group_k/.env").exists()) {
            dotenv = Dotenv.configure().directory("ca1_group_k").load();
        } else if (new java.io.File("../.env").exists()) {
            dotenv = Dotenv.configure().directory("..").load();
        } else {
            throw new RuntimeException(".env file not found in any expected location");
        }
    }
    
    /**
     * Gets an environment variable value.
     * 
     * @param key The environment variable key
     * @return The value, or null if not found
     */
    public static String get(String key) {
        return dotenv.get(key);
    }
    
    /**
     * Gets an environment variable value with a required check.
     * 
     * @param key The environment variable key
     * @return The value
     * @throws RuntimeException if the key is not found
     */
    public static String getRequired(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            throw new RuntimeException(key + " not found in .env file");
        }
        return value;
    }
}
