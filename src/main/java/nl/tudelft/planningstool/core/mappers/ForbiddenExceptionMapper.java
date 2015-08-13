package nl.tudelft.planningstool.core.mappers;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

/**
 * This ExceptionMapper maps {@link ForbiddenException ForbiddenExceptions} in such a way that the
 * client
 * receives a descriptive JSON response and HTTP status code.
 */
@Provider
public class ForbiddenExceptionMapper extends AbstractExceptionMapper<ForbiddenException> {

    @Override
    public Response.Status getStatusCode() {
        return FORBIDDEN;
    }

}
