package nl.tudelft.planningstool.core;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by tim on 8-7-15.
 */
public class RequestHandler extends ServletContextHandler {

    private final App app;

    public RequestHandler(App app) {
        this.app = app;
        this.addEventListener(new AppContextListener());
        this.addServlet(HttpServletDispatcher.class, "/");
    }

    private class AppContextListener extends GuiceResteasyBootstrapServletContextListener {

        @Override
        protected List<Module> getModules(ServletContext context) {
            return ImmutableList.of(new RequestServletModule());
        }

        @Override
        protected void withInjector(Injector injector) {
            final FilterHolder guiceFilterHolder = new FilterHolder(
                    injector.getInstance(GuiceFilter.class));
            RequestHandler.this.addFilter(guiceFilterHolder, "/*",
                    EnumSet.allOf(DispatcherType.class));
            RequestHandler.this.app.getInjectorAtomicReference().set(injector);
        }
    }
}
