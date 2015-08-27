package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Produces(value = MediaType.APPLICATION_JSON)
public class ResponseAPI {

    @Inject
    protected UserDAO userDAO;

    @Inject
    protected CourseDAO courseDAO;

    protected <T, R> ListResponse<R> createListResponse(Collection<T> occurrences, Function<T, R> mapper) {
        return ListResponse.with(
                occurrences.stream()
                        .map(mapper)
                        .collect(Collectors.toSet())
        );
    }

}
