package nci.security_fundamentals.auth;

import nci.security_fundamentals.server.db.User_repository;
import nci.security_fundamentals.server.models.User;

public class AuthService {

    private final User_repository userRepository;
    private final JwtService jwtService;
    PasswordEncryptor peq;

    public AuthService(User_repository userRepository) {
        this.userRepository = userRepository;
        jwtService = new JwtService();
        peq = new PasswordEncryptor();
    }

    public String registerUser(String username, String email, String password) {
        // Registration logic here
        try {
            if (!(username == null && email == null && password == null)) {
//                String encrypted = peq.encryptString(password);
                User newUser = new User(username, email, peq.encryptString(password));
                userRepository.createUser(newUser);
            } else {
                return "missing user information: email: " + email + ", username: " + username + ", password: " + password;
            }

        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }

        return "User registered successfully: " + username;
    }

    public String login(String username, String password) {
        try {
            User currUser = userRepository.findByUsername(username);
            if (currUser != null) {
                String psw = currUser.getPasswordHash();
                if (peq.decryptString(psw).equals(password)) { // peq.encryptString add in later when encryption of password is finished
                    String token = jwtService.getToken(currUser);
                    return token;
                } else {
                    return "Password does not match encrypted password";
                }
            } else {
                return "user not found";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Could not log in: " + e.getMessage();
        }
    }


    public User authenticateWithToken(String token) {
//        String tokenResult = jwtService.validateToken(token);
//        User user = null;
//        System.out.println("[DEBUG] Token validation result: " + tokenResult);
//        if(tokenResult.equals("LockTalk")){
//            String username = jwtService.getUsernameFromToken(token);
//            System.out.println("[DEBUG] Looking for username in DB: " + username);
//            user = userRepository.findByUsername(username);
//            if (user == null) {
//                System.out.println("[DEBUG] No user found for " + username);
//            }
//            else{
//                System.out.println("[DEBUG] User found: \" "+ user.getUsername());
//            }
//        }else{
//            return user;
//        }
//
//        return user;
        System.out.println("[DEBUG] Authenticating token...");
        String tokenResult = jwtService.validateToken(token);
        System.out.println("[DEBUG] Token validation result: " + tokenResult);

        if (tokenResult.startsWith("Invalid Token:")) {
            System.out.println("[DEBUG] Token invalid. Rejecting.");
            return null;
        }

        // Try username lookup first
        String username = jwtService.getUsernameFromToken(token);
        System.out.println("[DEBUG] Extracted username from token: " + username);

        User user = userRepository.findByUsername(username);

        // If not found by username, fall back to ObjectId lookup
        if (user == null) {
            System.out.println("[DEBUG] User not found by username, trying ObjectId lookup...");
            try {
                user = userRepository.findById(new org.bson.types.ObjectId(tokenResult));
            } catch (Exception e) {
                System.out.println("[DEBUG] Could not parse ObjectId: " + e.getMessage());
            }
        }

        if (user == null) {
            System.out.println("[DEBUG] Authentication failed — user not found.");
        } else {
            System.out.println("[DEBUG] User found: " + user.getUsername());
        }

        return user;
    }

    public boolean isTokenValid(String token) {
        String tokenResult = jwtService.validateToken(token);
        // validateToken returns the user ID if valid, or "Invalid Token: ..." if invalid
        return !tokenResult.startsWith("Invalid Token:");
    }
    
    public String getUsernameFromToken(String token) {
        return jwtService.getUsernameFromToken(token);
    }
}
