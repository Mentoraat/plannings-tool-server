package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.OccurrenceResponse;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Produces(value = MediaType.APPLICATION_JSON)
@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/occurrences")
public class UserOccurrenceAPI {

    @Inject
    private UserDAO userDAO;

    @Inject
    private CourseDAO courseDAO;

    @GET
    public ListResponse<OccurrenceResponse> get(@PathParam("userId") String userId) {
        return createListResponse(this.userDAO.getFromUUID(userId).getOccurrences());
    }

    @GET
    @Path("/courses/COURSE-{courseId: (\\d|\\w|-)+}")
    public ListResponse<OccurrenceResponse> getWithCourse(@PathParam("userId") String userId,
                                                @PathParam("courseId") String courseId) {
        final Course course = this.courseDAO.getFromUUID(courseId);

        Set<Occurrence> occurrences = this.userDAO.getFromUUID(userId).getOccurrences().stream()
                .filter(o -> o.getAssignment().getCourse().equals(course))
                .collect(Collectors.toSet());

        return createListResponse(occurrences);
    }

    private ListResponse<OccurrenceResponse> createListResponse(Collection<Occurrence> occurrences) {
        return ListResponse.with(
                occurrences.stream()
                .map(OccurrenceResponse::from)
                .collect(Collectors.toSet())
        );
    }

}
