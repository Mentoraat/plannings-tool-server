package nl.tudelft.planningstool.core.mappers;

import org.jboss.resteasy.spi.BadRequestException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import java.util.UUID;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class BadRequestExceptionMapper extends AbstractExceptionMapper<BadRequestException> {

    @Override
    public Response.Status getStatusCode() {
        return BAD_REQUEST;
    }

    @Override
    protected Response createResponse(Throwable exception, UUID id) {
        return super.createResponse(getActualCause(exception), id);
    }

    @Override
    protected void logException(Throwable exception, UUID id) {
        super.logException(getActualCause(exception), id);
    }

    private Throwable getActualCause(Throwable exception) {
        Throwable currentException = exception;

        while ((currentException = currentException.getCause()) != null) {
            if (currentException instanceof IllegalArgumentException) {
                return currentException;
            }
        }

        return exception;
    }
}

