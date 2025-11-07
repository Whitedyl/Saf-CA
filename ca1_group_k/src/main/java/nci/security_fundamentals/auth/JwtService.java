package nci.security_fundamentals.auth;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.github.cdimascio.dotenv.Dotenv;
import nci.security_fundamentals.models.User;

public class JwtService {

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24h
    private final Algorithm algorithm;

    public JwtService() {
        Dotenv dotenv = Dotenv.load();
        String secretKey = dotenv.get("JWT_SECRET_KEY");
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

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
            return "" + e.getMessage();
        }

        return token;
    }

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

    public String getUsernameFromToken(String token){
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
}

