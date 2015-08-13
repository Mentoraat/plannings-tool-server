package nl.tudelft.planningstool.api;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("main/")
public class MainAPI {

    private UserDAO userDAO;

    @Inject
    public MainAPI(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Path("test")
    public String test() {
        User user = new User();
        user.setName("Tim");
        user.setAccessToken("asdf");

        this.userDAO.persist(user);
        return "Succeeded!";
    }

    @GET
    @Path("get")
    public User get() {
        return this.userDAO.getFromId(1);
    }
}
