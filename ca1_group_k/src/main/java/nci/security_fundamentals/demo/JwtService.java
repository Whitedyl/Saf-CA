package nci.security_fundamentals.demo;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import nci.security_fundamentals.models.User;

/**
 * JWT Service - DEMO VERSION
 * Handles JSON Web Token creation and validation
 */
public class JwtService {
    
    private static final String SECRET_KEY = "demo-secret-key-change-in-production";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours
    private final Algorithm algorithm;
    
    public JwtService() {
        this.algorithm = Algorithm.HMAC256(SECRET_KEY);
    }
    
    public String generateToken(User user) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + EXPIRATION_TIME);
        
        return JWT.create()
                .withIssuer("LockTalk")
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
    
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("LockTalk")
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            System.err.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
    
    public String getUserIdFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("LockTalk")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            System.err.println("Cannot extract user ID: " + e.getMessage());
            return null;
        }
    }
    
    public String getUsernameFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("LockTalk")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        } catch (JWTVerificationException e) {
            System.err.println("Cannot extract username: " + e.getMessage());
            return null;
        }
    }
    
    public String getEmailFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("LockTalk")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            System.err.println("Cannot extract email: " + e.getMessage());
            return null;
        }
    }
    
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
    
    public Date getExpirationDate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (JWTVerificationException e) {
            System.err.println("Cannot get expiration date: " + e.getMessage());
            return null;
        }
    }
    
    public void printTokenInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            System.out.println("=== JWT Token Information ===");
            System.out.println("Issuer: " + jwt.getIssuer());
            System.out.println("Subject (User ID): " + jwt.getSubject());
            System.out.println("Username: " + jwt.getClaim("username").asString());
            System.out.println("Email: " + jwt.getClaim("email").asString());
            System.out.println("Issued At: " + jwt.getIssuedAt());
            System.out.println("Expires At: " + jwt.getExpiresAt());
            System.out.println("Is Expired: " + isTokenExpired(token));
            System.out.println("=============================");
        } catch (JWTVerificationException e) {
            System.err.println("Cannot decode token: " + e.getMessage());
        }
    }
}
