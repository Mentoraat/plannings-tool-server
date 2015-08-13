package nl.tudelft.planningstool.api;

import nl.tudelft.planningstool.database.controllers.UserDAO;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by tim on 14-8-15.
 */
public class MainAPITest {

    @Test
    public void test() {
        new MainAPI(Mockito.mock(UserDAO.class)).test();
    }
}
