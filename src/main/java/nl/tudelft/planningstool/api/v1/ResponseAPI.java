package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.database.controllers.AssignmentDAO;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class to provide general <a href="{@docRoot}/nl/tudelft/planningstool/database/controllers/package-summary.html#package_description">DAO's</a>.
 */
@Produces(value = MediaType.APPLICATION_JSON)
public class ResponseAPI {

    @Inject
    protected UserDAO userDAO;

    @Inject
    protected CourseDAO courseDAO;

    @Inject
    protected AssignmentDAO assignmentDAO;

    protected <T, R> ListResponse<R> createListResponse(Collection<T> responses, Function<T, R> mapper) {
        return ListResponse.with(
                responses.stream()
                        .map(mapper)
                        .collect(Collectors.toSet())
        );
    }

}
