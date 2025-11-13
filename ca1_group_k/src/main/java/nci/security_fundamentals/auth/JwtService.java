package nci.security_fundamentals.auth;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import nci.security_fundamentals.config.EnvConfig;
import nci.security_fundamentals.server.models.User;

/**
 * JwtService - JSON Web Token Manager
 * 
 * PURPOSE: Handles all JWT token operations (create, validate, extract claims)
 * USED BY: AuthService (doesn't interact directly with LoginHandler)
 * 
 * WHAT IS A JWT?
 * - A secure, signed token that contains user information
 * - Structure: Header.Payload.Signature
 * - Payload contains: user ID, username, email, expiration time
 * - Signature ensures token hasn't been tampered with
 * 
 * WHY JWT?
 * - Stateless authentication: No need to store sessions on server
 * - Self-contained: Token has all info needed to identify user
 * - Secure: Cryptographically signed with secret key
 * 
 * TOKEN LIFECYCLE:
 * 1. User logs in → JwtService.getToken() creates token
 * 2. Client stores token (in file, memory, etc.)
 * 3. Every request includes token
 * 4. Server calls JwtService.validateToken() to verify
 * 5. Extract user info with getUsernameFromToken()
 * 
 * SECURITY:
 * - Uses HMAC256 algorithm with secret key from .env
 * - Tokens expire after 24 hours
 * - If token is modified, signature verification fails
 */
public class JwtService {

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    private final Algorithm algorithm;  // HMAC256 signing algorithm

    /**
     * Constructor - Initializes JWT signing algorithm
     * Loads secret key from .env file (JWT_SECRET_KEY)
     * This key is used to sign and verify all tokens
     */
    public JwtService() {
        String secretKey = EnvConfig.getRequired("JWT_SECRET_KEY");
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    /**
     * Generate a JWT token for a user
     * 
     * TOKEN CONTENTS (Claims):
     * - Issuer: "LockTalk" (our app name)
     * - Subject: User's MongoDB ObjectId
     * - username: User's username
     * - email: User's email
     * - Issued At: Current timestamp
     * - Expires At: Current time + 24 hours
     * 
     * SIGNATURE: Created using HMAC256 with secret key
     * 
     * @param user The user to create token for
     * @return JWT token string
     */
    public String getToken(User user) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + EXPIRATION_TIME);
        String token = "";

        try {
            token = JWT.create()
                    .withIssuer("LockTalk")
                    .withSubject(user.getId().toString())
                    .withClaim("username", user.getUsername())
                    .withClaim("email", user.getEmail())
                    .withIssuedAt(now)
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

        } catch (Exception e) {
            // Invalid Signing configuration / Couldn't convert Claims.
            return e.getMessage();
        }

        return token;
    }

    /**
     * Validate a JWT token
     * 
     * VERIFICATION CHECKS:
     * 1. Signature is valid (token wasn't modified)
     * 2. Token was issued by "LockTalk"
     * 3. Token hasn't expired
     * 
     * @param token The JWT token to validate
     * @return User ID (subject) if valid, error message if invalid
     */
    public String validateToken(String token) {
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer("LockTalk")
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);

        } catch (JWTVerificationException exception) {
            return "Invalid Token: " + exception.getMessage();
        }

        return decodedJWT.getSubject();
    }

    /**
     * Check if token has expired
     * 
     * @return true if token is past expiration date
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            return expiresAt.before(new Date());
        } catch (JWTVerificationException e) {
            System.err.println("Cannot check expiration: " + e.getMessage());
            return true;
        }
    }

    public String getUserIdFromToken(String token) {
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer("LockTalk")
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);

        } catch (JWTVerificationException exception) {
            return "Invalid Token: " + exception.getMessage();
        }

        return decodedJWT.getId();
    }

    /**
     * Extract username from token
     * 
     * USE CASE: When you have a valid token and need to identify the user
     * Example: Showing "Logged in as: username" in UI
     * 
     * @param token The JWT token
     * @return Username string, or error message if token is invalid
     */
    public String getUsernameFromToken(String token) {
//        DecodedJWT decodedJWT;
//        try {
//            JWTVerifier verifier = JWT.require(algorithm)
//                    // specify any specific claim validations
//                    .withIssuer("LockTalk")
//                    // reusable verifier instance
//                    .build();
//
//            decodedJWT = verifier.verify(token);
//
//        } catch (JWTVerificationException exception) {
//            return "Invalid Token: " + exception.getMessage();
//        }
//
//        return decodedJWT.getId();
//    }
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("LockTalk")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("username").asString();

        } catch (JWTVerificationException exception) {
            return "Invalid Token: " + exception.getMessage();
        }
    }
}

