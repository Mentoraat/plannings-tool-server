package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.parameters.Credentials;
import nl.tudelft.planningstool.api.responses.TokenResponse;
import nl.tudelft.planningstool.api.security.NotAUserException;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.*;

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
    public TokenResponse authenticateUser(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Authenticate the user, issue a token and return a response
        try {
            User user = authenticate(username, password);

            return issueToken(user);
        }
        catch (Exception e) {
            // TODO: Add brute-force prevention
            throw new NotAuthorizedException("Invalid credentials.");
        }
    }

    @POST
    @Path("register")
    public TokenResponse registerUser(Credentials credentials) throws NoSuchAlgorithmException {
        requireStringNotEmpty(credentials.getUsername());
        requireStringNotEmpty(credentials.getPassword());

        if (this.userDAO.existsWithUsername(credentials.getUsername())) {
            throw new IllegalArgumentException("User already exists.");
        }

        User user = new User();
        user.setAdminStatus(User.AdminStatus.USER);
        user.setHashedPassword(this.hashPassword(credentials.getPassword()));
        user.setName(credentials.getUsername().toLowerCase());
        user.setUuid(UUID.randomUUID().toString());

        this.courseDAO.getAll()
            .stream().filter(c -> !c.getEdition().getCourseId().startsWith("USER-"))
            .forEach(course -> {
                CourseRelation rel = new CourseRelation();
                rel.setCourse(course);
                rel.setCourseRole(CourseRelation.CourseRole.STUDENT);
                user.addCourseRelation(rel);
            });

        this.userDAO.persist(user);

        return this.authenticateUser(credentials);
    }

    private void requireStringNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value is empty!");
        }
    }

    private TokenResponse issueToken(User user) {
        long oneDayFromNow = System.currentTimeMillis() + 86_400_000;
        String token = generateToken();

        // FIXME: Store validity in User table
        user.setAccessToken(token);
        userDAO.merge(user);

        TokenResponse response = new TokenResponse();
        response.setToken(token);
        response.setEndOfValidity(oneDayFromNow);
        response.setUuid(user.getUuid());

        return response;
    }

    private String generateToken() {
        Random random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private User authenticate(String username, String password) throws Exception {
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
