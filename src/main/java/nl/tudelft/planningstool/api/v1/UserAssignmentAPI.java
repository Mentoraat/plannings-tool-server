package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Maps;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * API end-point to provide assignments for an user.
 */
@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/courses/assignments")
public class UserAssignmentAPI extends ResponseAPI {

    /**
     * Get all the assignments for the provided user.
     * Returns only the assignments that have not been planned yet (e.g. do not have an occurrence relation with
     * the provided user).
     *
     * @param userId The id of the user.
     * @return A list of assigments.
     */
    @GET
    public ListResponse<AssignmentResponse> get(@PathParam("userId") String userId) {
        long now = System.currentTimeMillis();

        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> alreadyPlannedAssignments = user.getOccurrences().stream()
                .map(UserOccurrence::getAssignment)
                .collect(Collectors.toSet());

        Collection<Assignment> assignments = user.getCourses().stream()
                .map((c) -> c.getCourse().getAssignments())
                .flatMap(Collection::stream)
                .filter(a -> !alreadyPlannedAssignments.contains(a))
                .filter(a -> a.getDeadline() > now)
                .collect(Collectors.toSet());

        return createListResponse(assignments);
    }

    @GET
    @Path("/stats")
    public int[] getWithStatus(@PathParam("userId") String userId) {
        Map<UserOccurrence.OccurrenceStatus, AtomicInteger> map = Maps.newConcurrentMap();

        for (UserOccurrence.OccurrenceStatus status : UserOccurrence.OccurrenceStatus.values()) {
            map.put(status, new AtomicInteger());
        }

        User user = this.userDAO.getFromUUID(userId);
        user.getOccurrences().forEach(o -> {
            map.get(o.getStatus()).incrementAndGet();
        });

        long total = user.getCourses().stream()
            .map(CourseRelation::getCourse)
            .map(Course::getAssignments)
            .flatMap(Collection::stream)
            .count();

        int finished = map.get(UserOccurrence.OccurrenceStatus.FINISHED).get();
        return new int[] {finished, (int) total - finished};
    }

    private ListResponse<AssignmentResponse> createListResponse(Collection<Assignment> assignments) {
        return createListResponse(assignments, AssignmentResponse::from);
    }
}
