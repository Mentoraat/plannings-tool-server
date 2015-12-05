package hibernate;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import nl.tudelft.planningstool.database.DatabaseTestModule;
import nl.tudelft.planningstool.database.bootstrapper.BootstrapRule;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hibernate.exception.ConstraintViolationException;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class UserCreationTest {

    @Rule
    @Inject
    public BootstrapRule bootstrapRule;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Inject
    private UserDAO userDAO;

    @Test
    @TestBootstrap("users/no_user.json")
    public void can_persist_user() {
        User user = createUser("Admin", "asdf");

        this.userDAO.persist(user);

        assertEquals(1, user.getId().intValue());
    }

    @Test
    @TestBootstrap("users/one_user.json")
    public void can_retrieve_user() {
        User user = this.userDAO.getFromId(1);

        assertNotNull(user);
    }

    @Test
    @TestBootstrap("users/one_user.json")
    public void can_persist_new_user_gets_incremented_id() {
        User user = createUser("Admin2", "fdas");

        this.userDAO.persist(user);

        assertEquals(2, user.getId().intValue());
    }

    @Test
    @TestBootstrap("users/one_user.json")
    public void can_not_persist_user_with_same_name() {
        User user = createUser("Admin", "fdas");

        expected.expectCause(new BaseMatcher<Throwable>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ConstraintViolationException;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Name must violate unique constraint on user");
            }
        });

        this.userDAO.persist(user);
    }

    @Test
    @TestBootstrap("users/one_user.json")
    public void can_not_persist_user_with_same_accesstoken() {
        User user = createUser("Admin2", "asdf");

        expected.expectCause(new BaseMatcher<Throwable>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ConstraintViolationException;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Accesstoken must violate unique constraint on user");
            }
        });

        this.userDAO.persist(user);
    }

    @Test
    @TestBootstrap("users/one_user.json")
    public void can_persist_user_with_no_accesstoken() {
        User user = createUser("Admin2", null);

        this.userDAO.persist(user);

        assertEquals(2, user.getId().intValue());
    }

    private User createUser(String name, String accessToken) {
        User user = new User();

        user.setAccessToken(accessToken);
        user.setName(name);
        user.setCourses(Sets.newHashSet());
        user.setHashedPassword("asdf");

        return user;
    }
}
