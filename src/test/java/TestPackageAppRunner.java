import com.google.inject.Injector;

import nl.tudelft.planningstool.core.App;
import nl.tudelft.planningstool.database.bootstrapper.Bootstrapper;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The {@link TestPackageAppRunner} uses the embedded H2 database rather than
 * an initialized Postgres environment.
 */
public class TestPackageAppRunner {
    
    private TestPackageAppRunner() {
        // You may only run the main method.
    }

    public static void main(final String... args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final App app = new App();
        app.startServer();

        app.getInjectorAtomicReference().get()
                .getInstance(Bootstrapper.class)
                .parseFromResource("default.json");

        app.joinThread();
    }
}
