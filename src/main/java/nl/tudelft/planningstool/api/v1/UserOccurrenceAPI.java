package nl.tudelft.planningstool.api.v1;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.CourseOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/occurrences")
public class UserOccurrenceAPI extends ResponseAPI {

    @GET
    public Collection<? super OccurrenceResponse> get(@PathParam("userId") String userId,
                                                          @Form TimeSlot timeSlot) {
        final User user = this.userDAO.getFromUUID(userId);
        Set<? super OccurrenceResponse> occurrences = user.getOccurrences().stream()
                .filter(o -> o.getStart_time() >= timeSlot.getStart())
                .filter(o -> o.getEnd_time() <= timeSlot.getEnd())
                .map(UserOccurrenceResponse::from)
                .collect(Collectors.toSet());

        occurrences.addAll(
                user.getCourses().stream()
                        .map((c) -> c.getCourse().getOccurrences())
                        .flatMap(Collection::stream)
                        .filter(o -> o.getStart_time() >= timeSlot.getStart())
                        .filter(o -> o.getEnd_time() <= timeSlot.getEnd())
                        .map(CourseOccurrenceResponse::from)
                        .collect(Collectors.toSet())
        );

        return occurrences;
    }

    @POST
    public UserOccurrenceResponse create(@PathParam("userId") String userId,
                                         UserOccurrenceResponse data) {
        UserOccurrence occurrence = new UserOccurrence();
        occurrence.setStart_time(data.getStartTime());
        occurrence.setEnd_time(data.getEndTime());

        AssignmentResponse assignment = data.getAssignment();
        occurrence.setAssignment(
                this.assignmentDAO.getFromCourseWithId(
                        assignment.getCourse().getEdition().getCourseId(),
                        assignment.getCourse().getEdition().getYear(),
                        assignment.getId()
                )
        );

        this.userDAO.getFromUUID(userId).addOccurrence(occurrence);

        return UserOccurrenceResponse.from(occurrence);
    }

    @PUT
    public UserOccurrenceResponse update(@PathParam("userId") String userId,
                                         UserOccurrenceResponse data) {
        User user = this.userDAO.getFromUUID(userId);
        AssignmentResponse assignmentData = data.getAssignment();

        List<UserOccurrence> occurrences = user.getOccurrences().stream().filter((o) -> {
            Assignment assignment = o.getAssignment();
            CourseEdition courseEdition = assignment.getCourse().getEdition();

            return assignment.getId().equals(assignmentData.getId())
                    && courseEdition.getYear() == assignmentData.getCourse().getEdition().getYear()
                    && courseEdition.getCourseId().equals(assignmentData.getCourse().getEdition().getCourseId());
        }).collect(Collectors.toList());

        if (occurrences.isEmpty()) {
            throw new IllegalArgumentException("Occurrence not found.");
        }

        UserOccurrence occurrence = occurrences.get(0);
        occurrence.setEnd_time(data.getEndTime());
        occurrence.setStart_time(data.getStartTime());

        log.info("Updated occurrence {}", occurrence);

        this.userDAO.merge(user);

        return data;
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
