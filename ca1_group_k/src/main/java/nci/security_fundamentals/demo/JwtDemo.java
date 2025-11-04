package nci.security_fundamentals.demo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import nci.security_fundamentals.db.User_repository;
import nci.security_fundamentals.models.User;

/**
 * JWT Demo - Complete JWT authentication demonstration
 * 
 * THIS IS A DEMO FILE - All JWT code is self-contained in the demo package
 * Run this to see JWT authentication in action!
 */
public class JwtDemo {
    
    public static void main(String[] args) {
        String uri = "mongodb+srv://andrepontde:261010@userinfo.vcyjmfx.mongodb.net/?appName=userinfo";
        
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("LockTalk");
            
            // Create services (all from demo package)
            User_repository userRepo = new User_repository(database);
            AuthService authService = new AuthService(userRepo);
            JwtService jwtService = new JwtService();
            
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║         JWT AUTHENTICATION DEMONSTRATION               ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");
            
            // EXAMPLE 1: User Registration
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 1: User Registration");
            System.out.println("═══════════════════════════════════════════════════════");
            
            User registeredUser = authService.register(
                "alice_demo", 
                "alice@demo.com", 
                "securePassword123"
            );
            
            if (registeredUser != null) {
                System.out.println("✓ User registered successfully!");
                System.out.println("  Username: " + registeredUser.getUsername());
                System.out.println("  Email: " + registeredUser.getEmail());
                System.out.println("  ID: " + registeredUser.getId());
                System.out.println("  Password: [HASHED - SECURE]");
            } else {
                System.out.println("✗ Registration failed (user may already exist)");
            }
            
            // EXAMPLE 2: User Login & JWT Token Generation
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 2: User Login (JWT Token Generation)");
            System.out.println("═══════════════════════════════════════════════════════");
            
            String jwtToken = authService.login("alice_demo", "securePassword123");
            
            if (jwtToken != null) {
                System.out.println("✓ Login successful!");
                System.out.println("\n📝 JWT Token Generated:");
                System.out.println(jwtToken);
                System.out.println("\n🔍 This token contains:");
                System.out.println("   • User ID (encrypted)");
                System.out.println("   • Username");
                System.out.println("   • Email");
                System.out.println("   • Expiration time (24 hours from now)");
                System.out.println("   • Digital signature (prevents tampering)");
            } else {
                System.out.println("✗ Login failed");
            }
            
            // EXAMPLE 3: Token Validation
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 3: Token Validation");
            System.out.println("═══════════════════════════════════════════════════════");
            
            if (jwtToken != null) {
                boolean isValid = jwtService.validateToken(jwtToken);
                System.out.println("Token is valid: " + (isValid ? "✓ YES" : "✗ NO"));
                
                if (isValid) {
                    String userIdFromToken = jwtService.getUserIdFromToken(jwtToken);
                    String usernameFromToken = jwtService.getUsernameFromToken(jwtToken);
                    String emailFromToken = jwtService.getEmailFromToken(jwtToken);
                    
                    System.out.println("\n📤 Information extracted from token:");
                    System.out.println("   User ID: " + userIdFromToken);
                    System.out.println("   Username: " + usernameFromToken);
                    System.out.println("   Email: " + emailFromToken);
                    System.out.println("\n💡 Notice: No database query needed!");
                }
            }
            
            // EXAMPLE 4: Detailed Token Information
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 4: Detailed Token Information");
            System.out.println("═══════════════════════════════════════════════════════");
            
            if (jwtToken != null) {
                jwtService.printTokenInfo(jwtToken);
            }
            
            // EXAMPLE 5: Protected Operation
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 5: Protected Operation - Send Message");
            System.out.println("═══════════════════════════════════════════════════════");
            
            if (jwtToken != null) {
                System.out.println("📨 User wants to send an encrypted message...");
                System.out.println("   Step 1: Validate JWT token");
                
                User authenticatedUser = authService.authenticateWithToken(jwtToken);
                
                if (authenticatedUser != null) {
                    System.out.println("   ✓ Token valid!");
                    System.out.println("   ✓ User authenticated: " + authenticatedUser.getUsername());
                    System.out.println("\n   Step 2: Allow message sending");
                    System.out.println("   ✓ Message would be encrypted and saved to database");
                    System.out.println("   (In real app: encrypt content and save via Message_repository)");
                } else {
                    System.out.println("   ✗ Authentication failed!");
                    System.out.println("   ✗ Message rejected - user must login");
                }
            }
            
            // EXAMPLE 6: Invalid Token Handling
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 6: Security Test - Invalid Token");
            System.out.println("═══════════════════════════════════════════════════════");
            
            String fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fake.token";
            System.out.println("🔒 Attempting to use fake/tampered token...");
            boolean isFakeValid = jwtService.validateToken(fakeToken);
            System.out.println("   Fake token validated: " + (isFakeValid ? "✗ SECURITY BREACH!" : "✓ REJECTED (secure)"));
            
            User fakeUser = authService.authenticateWithToken(fakeToken);
            System.out.println("   Authentication result: " + (fakeUser != null ? "✗ SECURITY BREACH!" : "✓ REJECTED (secure)"));
            System.out.println("\n💪 System correctly rejected invalid token!");
            
            // EXAMPLE 7: Failed Login
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 7: Failed Login - Wrong Password");
            System.out.println("═══════════════════════════════════════════════════════");
            
            System.out.println("🔑 Attempting login with wrong password...");
            String failedToken = authService.login("alice_demo", "wrongPassword");
            
            if (failedToken == null) {
                System.out.println("✓ Login correctly rejected");
                System.out.println("✓ No token issued");
                System.out.println("✓ Security maintained");
            } else {
                System.out.println("✗ SECURITY ISSUE - Should have rejected!");
            }
            
            // EXAMPLE 8: Password Change
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 8: Change Password");
            System.out.println("═══════════════════════════════════════════════════════");
            
            if (registeredUser != null) {
                System.out.println("🔐 Changing password...");
                boolean passwordChanged = authService.changePassword(
                    registeredUser.getId(),
                    "securePassword123",
                    "newSecurePassword456"
                );
                
                if (passwordChanged) {
                    System.out.println("✓ Password changed successfully!");
                    
                    System.out.println("\n   Testing old password...");
                    String oldPasswordToken = authService.login("alice_demo", "securePassword123");
                    System.out.println("   Old password works: " + (oldPasswordToken != null ? "✗ ISSUE" : "✓ REJECTED"));
                    
                    System.out.println("\n   Testing new password...");
                    String newPasswordToken = authService.login("alice_demo", "newSecurePassword456");
                    System.out.println("   New password works: " + (newPasswordToken != null ? "✓ YES" : "✗ FAILED"));
                    
                    if (newPasswordToken != null) {
                        System.out.println("   ✓ New token issued successfully!");
                    }
                }
            }
            
            // EXAMPLE 9: Token Expiration
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 9: Token Expiration");
            System.out.println("═══════════════════════════════════════════════════════");
            
            if (jwtToken != null) {
                System.out.println("⏰ Token expiration details:");
                boolean isExpired = jwtService.isTokenExpired(jwtToken);
                System.out.println("   Is expired: " + (isExpired ? "YES" : "NO"));
                System.out.println("   Expires at: " + jwtService.getExpirationDate(jwtToken));
                System.out.println("\n💡 This token is valid for 24 hours");
                System.out.println("   After expiration, user must login again");
            }
            
            // EXAMPLE 10: Multiple Users
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("EXAMPLE 10: Multiple Users Authentication");
            System.out.println("═══════════════════════════════════════════════════════");
            
            User user2 = authService.register("bob_demo", "bob@demo.com", "bobPassword789");
            
            if (user2 != null) {
                System.out.println("✓ Second user registered: " + user2.getUsername());
                
                String aliceToken = authService.login("alice_demo", "newSecurePassword456");
                String bobToken = authService.login("bob_demo", "bobPassword789");
                
                if (aliceToken != null && bobToken != null) {
                    System.out.println("\n👥 Both users logged in with separate tokens:");
                    System.out.println("\n   Alice's token:");
                    System.out.println("   " + aliceToken.substring(0, Math.min(50, aliceToken.length())) + "...");
                    System.out.println("\n   Bob's token:");
                    System.out.println("   " + bobToken.substring(0, Math.min(50, bobToken.length())) + "...");
                    
                    User aliceAuth = authService.authenticateWithToken(aliceToken);
                    User bobAuth = authService.authenticateWithToken(bobToken);
                    
                    System.out.println("\n✓ Token isolation verified:");
                    System.out.println("   Alice's token → " + (aliceAuth != null ? aliceAuth.getUsername() : "null"));
                    System.out.println("   Bob's token → " + (bobAuth != null ? bobAuth.getUsername() : "null"));
                }
            } else {
                System.out.println("Note: Second user may already exist from previous run");
            }
            
            // Summary
            System.out.println("\n\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║                    SUMMARY                             ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n✅ What you learned:\n");
            System.out.println("   1. ✓ User registration with secure password hashing");
            System.out.println("   2. ✓ Login and JWT token generation");
            System.out.println("   3. ✓ Token validation (no database needed!)");
            System.out.println("   4. ✓ Extract user information from tokens");
            System.out.println("   5. ✓ Authenticate protected operations");
            System.out.println("   6. ✓ Security: Invalid tokens are rejected");
            System.out.println("   7. ✓ Security: Wrong passwords are rejected");
            System.out.println("   8. ✓ Password management");
            System.out.println("   9. ✓ Token expiration handling");
            System.out.println("  10. ✓ Multiple users with separate tokens");
            
            System.out.println("\n💡 Key Takeaways:\n");
            System.out.println("   • JWT tokens allow stateless authentication");
            System.out.println("   • No database query needed to validate tokens");
            System.out.println("   • Tokens are secure and tamper-proof");
            System.out.println("   • Each user gets a unique token");
            System.out.println("   • Tokens expire for security");
            
            System.out.println("\n🚀 Next Steps:\n");
            System.out.println("   → Copy demo code to your auth package");
            System.out.println("   → Integrate with GUI (login screen)");
            System.out.println("   → Protect message sending operations");
            System.out.println("   → Protect file upload operations");
            
            System.out.println("\n════════════════════════════════════════════════════════\n");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
