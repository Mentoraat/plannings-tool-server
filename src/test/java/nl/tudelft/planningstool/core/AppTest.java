package nl.tudelft.planningstool.core;

import com.google.inject.Inject;
import org.junit.Test;
import util.TestBase;

public class AppTest extends TestBase {

    @Inject
    private App app;

    @Test
    public void canBootUpAndShutdown() throws Exception {
        app.startServer();
        app.stopServer();
    }

}
