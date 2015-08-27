package nl.tudelft.planningstool.api.v1;

import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;

@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/courses/assignments")
public class UserAssignmentAPI extends ResponseAPI {

    @GET
    public ListResponse<AssignmentResponse> get(@PathParam("userId") String userId) {
        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> alreadyPlannedAssignments = user.getOccurrences().stream()
                .map(Occurrence::getAssignment)
                .collect(Collectors.toSet());

        Collection<Assignment> assignments = user.getCourses().stream()
                .map((c) -> c.getCourse().getAssignments())
                .flatMap(Collection::stream)
                .filter((a) -> !alreadyPlannedAssignments.contains(a))
                .collect(Collectors.toSet());

        return createListResponse(assignments);
    }

    private ListResponse<AssignmentResponse> createListResponse(Collection<Assignment> assignments) {
        return createListResponse(assignments, AssignmentResponse::from);
    }
}
