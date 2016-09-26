package nl.tudelft.planningstool.api.v1;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.parameters.Credentials;
import nl.tudelft.planningstool.api.parameters.Registration;
import nl.tudelft.planningstool.api.responses.TokenResponse;
import nl.tudelft.planningstool.api.security.NotAUserException;
import nl.tudelft.planningstool.database.DbModule;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.jboss.resteasy.annotations.Form;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    public TokenResponse registerUser(Registration registration) throws NoSuchAlgorithmException {
        requireStringNotEmpty(registration.getUsername());
        requireStringNotEmpty(registration.getPassword());

        if (this.userDAO.existsWithUsername(registration.getUsername())) {
            throw new IllegalArgumentException("User already exists.");
        }

        if (this.userDAO.existsWithEmail(registration.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (this.userDAO.existsWithStudentNumber(registration.getStudentnumber())) {
            throw new IllegalArgumentException("Student number already exists");
        }

        User user = new User();
        user.setAdminStatus(User.AdminStatus.USER);
        user.setHashedPassword(this.hashPassword(registration.getPassword()));
        user.setName(registration.getUsername().toLowerCase());
        user.setUuid(UUID.randomUUID().toString());
        user.setEmail(registration.getEmail());
        user.setStudentNumber(registration.getStudentnumber());

        this.courseDAO.getAll()
            .stream().filter(c -> !c.getEdition().getCourseId().startsWith("USER-"))
            .forEach(course -> {
                CourseRelation rel = new CourseRelation();
                rel.setCourse(course);
                rel.setCourseRole(CourseRelation.CourseRole.STUDENT);
                user.addCourseRelation(rel);
            });

        try {
            this.userDAO.persist(user);
        } catch (Exception ignored) {
            log.debug("Exception while registering registration {}", registration);
            throw new IllegalArgumentException("Failed processing user registration");
        }

        return this.authenticateUser(registration);
    }

    @Data
    private class ResetPasswordForm {
        String userName;
        String email;
    }

    @POST
    @Path("/resetpassword")
    public void resetPassword(ResetPasswordForm passwordForm) {
        User user = this.userDAO.getFromUsername(passwordForm.getUserName());

        if (!user.getEmail().equals(passwordForm.getEmail())) {
            throw new IllegalArgumentException("Invalid combination of username and email address");
        }

        String token = new BigInteger(130, new SecureRandom()).toString(32);

        user.setResetToken(token);
        // Set validity for 24 hours from now.
        user.setResetTokenValidity(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        this.userDAO.merge(user);

        try {
            Properties props = getProperties();
            Session session = Session.getInstance(props, null);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom("noreply@planningstool.ewi.tudelft.nl");
            msg.setRecipients(Message.RecipientType.TO, user.getEmail());
            msg.setSubject("Planningstool password reset token");
            msg.setSentDate(new Date());
            msg.setText("Dear " + user.getName() + ",\n\nYour reset token is " + token + "\n\nRegards,\nPlanningstool admins");
            Transport.send(msg, props.getProperty("user"), props.getProperty("password"));
        } catch (MessagingException | IOException mex) {
            log.error("send failed, exception: " + mex);
        }
    }

    private Properties getProperties() throws IOException {
        try (InputStream stream = DbModule.class.getResourceAsStream("/mail.properties")) {
            final Properties properties = new Properties();
            Preconditions.checkNotNull(stream, "Persistence properties not found");
            properties.load(stream);
            return properties;
        }
    }

    @POST
    @Path("/resetpassword/{resetToken: .+}")
    public void setNewPassword(Credentials credentials, @PathParam("resetToken") String providedResetToken) throws NoSuchAlgorithmException {
        User user = this.userDAO.getFromUsername(credentials.getUsername());

        if (user.getResetToken() == null || user.getResetTokenValidity() < System.currentTimeMillis() || user.getResetToken().equals(providedResetToken) ) {
            throw new IllegalArgumentException("illegal reset token provided");
        }

        user.setHashedPassword(this.hashPassword(credentials.getPassword()));

        // Deauthorize reset token
        user.setResetToken(null);
        user.setResetTokenValidity(0L);

        this.userDAO.merge(user);
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
