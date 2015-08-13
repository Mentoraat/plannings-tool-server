package nl.tudelft.planningstool.core;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.slf4j.bridge.SLF4JBridgeHandler;

@Slf4j
public class App {

    public static final int PORT = 9000;
    final Server server;

    public App() {
        this.server = new Server(PORT);
        this.server.setSessionIdManager(new HashSessionIdManager());
        this.server.setHandler(this.attachHandlers());
    }

    private ContextHandlerCollection attachHandlers() {
        final RequestHandler requestHandler = new RequestHandler(this);

        final ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.addContext("/", "/").setHandler(requestHandler);

        return handlers;
    }

    /**
     * Starts the {@link App} server.
     *
     * @throws Exception
     *             In case the server could not be started.
     */
    public void startServer() throws Exception {
        this.server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
    }

    /**
     * Joins the {@link App} server.
     *
     * @throws InterruptedException
     *             if the joined thread is interrupted
     *             before or during the merging.
     */
    public void joinThread() throws InterruptedException {
        this.server.join();
    }

    /**
     * Stops the {@link App} server.
     */
    public void stopServer() {
        try {
            this.server.stop();
        } catch (final Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    public static void main(String... args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final App app = new App();
        app.startServer();
        app.joinThread();
    }
}
