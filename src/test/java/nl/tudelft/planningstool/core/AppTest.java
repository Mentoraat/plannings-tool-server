package nl.tudelft.planningstool.core;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
public class AppTest {

    @Inject
    private App app;

    @Test
    public void canBootUpAndShutdown() throws Exception {
        app.startServer();
        app.stopServer();
    }

}
