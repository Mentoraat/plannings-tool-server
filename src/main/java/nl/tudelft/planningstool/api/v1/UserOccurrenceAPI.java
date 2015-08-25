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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Produces(value = MediaType.APPLICATION_JSON)
@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/courses/COURSE-{courseId: (\\d|\\w|-)+}")
@RequestScoped
public class UserOccurrenceAPI {

    private final UserDAO userDAO;
    private final CourseDAO courseDAO;

    @Inject
    public UserOccurrenceAPI(UserDAO userDAO,
                             CourseDAO courseDAO) {
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
    }

    @GET
    public ListResponse<OccurrenceResponse> get(@PathParam("userId") String userId,
                                                @PathParam("courseId") String courseId) {
        Course course = this.courseDAO.getFromUUID(UUID.fromString(courseId));

        Set<Occurrence> occurrences = this.userDAO.getFromUUID(UUID.fromString(userId)).getOccurrences().stream()
                .filter(o -> o.getAssignment().getCourse().equals(course))
                .collect(Collectors.toSet());

        Set<OccurrenceResponse> responses = occurrences.stream()
                .map(OccurrenceResponse::from)
                .collect(Collectors.toSet());

        return ListResponse.with(responses);
    }
}
