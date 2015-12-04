package nl.tudelft.planningstool.api.v1;


import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.parameters.Credentials;
import nl.tudelft.planningstool.api.responses.TokenResponse;
import nl.tudelft.planningstool.api.security.NotAUserException;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;


/**
 * Provides Authentication API endpoints.
 */
@Path("v1/authentication")
@Slf4j
public class AuthenticationAPI extends ResponseAPI{

    @Inject
    protected UserDAO userDAO;

    @POST
    public Response authenticateUser(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Authenticate the user, issue a token and return a response
        try {
            User user = authenticate(username, password);

            TokenResponse token = issueToken(user);

            return Response.ok(token).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("register")
    public Response registerUser(Credentials credentials) throws NoSuchAlgorithmException {
        if (this.userDAO.existsWithUsername(credentials.getUsername())) {
            throw new IllegalArgumentException("User already exists.");
        }

        User user = new User();
        user.setAdminStatus(User.AdminStatus.USER);
        user.setHashedPassword(this.hashPassword(credentials.getPassword()));
        user.setName(credentials.getUsername());
        user.setUuid(UUID.randomUUID().toString());

        this.userDAO.persist(user);

        return this.authenticateUser(credentials);
    }

    private TokenResponse issueToken(User user) {
        TokenResponse response = new TokenResponse();
        long oneDayFromNow = System.currentTimeMillis() + 86_400_000;
        String token = generateToken();

        response.setToken(token);
        response.setEndOfValidity(oneDayFromNow);
        response.setUuid(user.getUuid());

        // FIXME: Store validity in User table
        user.setAccessToken(token);
        userDAO.merge(user);

        return response;
    }

    private String generateToken() {
        Random random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private User authenticate(String username, String password) throws Exception{
        // Fetch user with given username
        User user = userDAO.getFromUsername(username);

        if(user == null || !user.getHashedPassword().equals(hashPassword(password))) {
            throw new NotAUserException("Invalid authentication");
        }

        return user;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] output = digest.digest(password.getBytes());

        digest.update(output);
        return new BigInteger(1, digest.digest()).toString();
    }

}
