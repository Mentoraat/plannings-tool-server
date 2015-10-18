package nl.tudelft.planningstool.api.v1;

import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;
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

        return this.getWithTime(userId, assignment -> assignment.getDeadline() < now);
    }

    @GET
    @Path("/status/{status}")
    public ListResponse<AssignmentResponse> getWithStatus(@PathParam("userId") String userId,
                                                          @PathParam("status") UserOccurrence.OccurrenceStatus status) {
        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> unfinishedAssignments = user.getOccurrences().stream()
                .filter((o) -> o.getStatus() == status)
                .map(UserOccurrence::getAssignment)
                .collect(Collectors.toSet());

        return createListResponse(unfinishedAssignments);
    }

    @GET
    @Path("/missed")
    public ListResponse<AssignmentResponse> getMissed(@PathParam("userId") String userId) {
        long now = System.currentTimeMillis();

        return this.getWithTime(userId, assignment -> assignment.getDeadline() > now);
    }

    private ListResponse<AssignmentResponse> getWithTime(String userId, Predicate<? super Assignment> filter) {
        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> alreadyPlannedAssignments = user.getOccurrences().stream()
                .map(UserOccurrence::getAssignment)
                .collect(Collectors.toSet());

        Collection<Assignment> assignments = user.getCourses().stream()
                .map((c) -> c.getCourse().getAssignments())
                .flatMap(Collection::stream)
                .filter((a) -> !alreadyPlannedAssignments.contains(a))
                .filter(filter)
                .collect(Collectors.toSet());

        return createListResponse(assignments);
    }

    private ListResponse<AssignmentResponse> createListResponse(Collection<Assignment> assignments) {
        return createListResponse(assignments, AssignmentResponse::from);
    }
}
