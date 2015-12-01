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


/**
 * Provides Authentication API endpoints.
 */
@Path("v1/authentication")
@Slf4j
public class AuthenticationAPI extends ResponseAPI{

    @Inject
    protected UserDAO userDAO;

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Authenticate the user, issue a token and return a response
        try {
            authenticate(username, password);

            TokenResponse token = issueToken(username);

            return Response.ok(token).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private TokenResponse issueToken(String username) {
        TokenResponse response = new TokenResponse();
        long oneDayFromNow = System.currentTimeMillis() + 86_400_000;
        String token = generateToken(username);

        response.setToken(token);
        response.setEndOfValidity(oneDayFromNow);

        // FIXME: Store validity in User table
        User user = userDAO.getFromUsername(username);
        user.setAccessToken(token);
        userDAO.merge(user);

        return response;
    }

    private String generateToken(String username) {
        return "AAAA-BBBB-CCCC-DDDD"; //FIXME

//        Random random = new SecureRandom();
//        return new BigInteger(130, random).toString(32);
    }

    private void authenticate(String username, String password) throws Exception{
        // Fetch user with given username
        User user = userDAO.getFromUsername(username);

        // Hash password with SHA-512
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] output = digest.digest(password.getBytes());

        digest.update(output);
        BigInteger bg = new BigInteger(1, digest.digest());

        if(user == null || !user.getHashedPassword().equals(bg.toString())) {
            throw new NotAUserException("Invalid authentication");
        }
        return;
    }

}
