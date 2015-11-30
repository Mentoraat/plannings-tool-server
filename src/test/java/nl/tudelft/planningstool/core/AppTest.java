package nl.tudelft.planningstool.core;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.DatabaseTestModule;
import nl.tudelft.planningstool.database.bootstrapper.BootstrapRule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class AppTest {

    @Rule
    @Inject
    public BootstrapRule bootstrapRule;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Inject
    private App app;

    @Test
    public void canBootUpAndShutdown() throws Exception {
        app.startServer();
        app.stopServer();
    }

}
