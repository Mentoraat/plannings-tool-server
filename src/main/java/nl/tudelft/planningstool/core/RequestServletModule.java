package nl.tudelft.planningstool.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.servlet.ServletModule;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.guice.ext.JaxrsModule;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;

/**
 * Created by tim on 8-7-15.
 */
@Slf4j
public class RequestServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        this.install(new JaxrsModule());
        this.requireBinding(ObjectMapper.class);
        this.bindAPI();
    }

    private void bindAPI() {
        this.bindClassesAnnotatedWithInPackage("nl.tudelft.planningstool.api", Path.class);
        this.bindClassesAnnotatedWithInPackage("nl.tudelft.planningstool.core.mappers", Provider.class);
        this.bindClassesAnnotatedWithInPackage("nl.tudelft.planningstool.api.filters", Provider.class);
    }

    private void bindClassesAnnotatedWithInPackage(final String packageName,
                                                   final Class<? extends Annotation> annotation) {
        final Reflections reflections = new Reflections(packageName);

        for (final Class<?> clazz : reflections.getTypesAnnotatedWith(annotation)) {
            this.bind(clazz);
            log.info("Registering class {}", clazz);
        }
    }
}
