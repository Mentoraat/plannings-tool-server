package nl.tudelft.planningstool.core.mappers;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * This ExceptionMapper maps {@link ForbiddenException ForbiddenExceptions} in such a way that the
 * client
 * receives a descriptive JSON response and HTTP status code.
 */
@Provider
public class NotAuthorizedExceptionMapper extends AbstractExceptionMapper<NotAuthorizedException> {

    @Override
    public Response.Status getStatusCode() {
        return UNAUTHORIZED;
    }

}
