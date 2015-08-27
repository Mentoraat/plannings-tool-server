package nl.tudelft.planningstool.api.v1;

import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.CourseOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/occurrences")
public class UserOccurrenceAPI extends ResponseAPI {

    @GET
    public ListResponse<? extends OccurrenceResponse> get(@PathParam("userId") String userId) {
        final User user = this.userDAO.getFromUUID(userId);
        Set<OccurrenceResponse> occurrences = user.getOccurrences().stream().map(UserOccurrenceResponse::from).collect(Collectors.toSet());

        occurrences.addAll(
                user.getCourses().stream()
                        .map((c) -> c.getCourse().getOccurrences())
                        .flatMap(Collection::stream)
                        .map(CourseOccurrenceResponse::from)
                        .collect(Collectors.toSet())
        );

        return ListResponse.with(occurrences);
    }

    @GET
    @Path("/courses/COURSE-{courseId: (\\d|\\w|-)+}")
    public ListResponse<UserOccurrenceResponse> getWithCourse(@PathParam("userId") String userId,
                                                @PathParam("courseId") String courseId) {
        final Course course = this.courseDAO.getFromUUID(courseId);

        Set<UserOccurrence> occurrences = this.userDAO.getFromUUID(userId).getOccurrences().stream()
                .filter(o -> o.getAssignment().getCourse().equals(course))
                .collect(Collectors.toSet());

        return createListResponse(occurrences);
    }

    private ListResponse<UserOccurrenceResponse> createListResponse(Collection<UserOccurrence> occurrences) {
        return createListResponse(occurrences, UserOccurrenceResponse::from);
    }

}
