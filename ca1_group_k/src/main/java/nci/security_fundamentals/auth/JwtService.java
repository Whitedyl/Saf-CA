package nci.security_fundamentals.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtService {

    // public String getToken(String data) {
    //     try {
    //         Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
    //         String token = JWT.create()
    //                 .withIssuer("auth0")
    //                 .sign(algorithm);
    //     } catch (JWTCreationException exception) {
    //         // Invalid Signing configuration / Couldn't convert Claims.
    //     }
    // }

    // public String validateToken(String token) {
    //     DecodedJWT decodedJWT;
    //     try {
    //         Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
    //         JWTVerifier verifier = JWT.require(algorithm)
    //                 // specify any specific claim validations
    //                 .withIssuer("auth0")
    //                 // reusable verifier instance
    //                 .build();

    //         decodedJWT = verifier.verify(token);
    //     } catch (JWTVerificationException exception) {
    //         // Invalid signature/claims
    //     }
    // }
}
