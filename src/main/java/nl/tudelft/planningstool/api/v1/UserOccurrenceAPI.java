package nl.tudelft.planningstool.api.v1;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.CourseOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Data
    @NoArgsConstructor
    static class OccurrenceData {

        private long startTime;

        private long endTime;

        private int assignmentId;

        private String courseId;

        private int courseYear;

    }

    @POST
    public UserOccurrenceResponse create(@PathParam("userId") String userId,
                                         OccurrenceData data) {
        UserOccurrence occurrence = new UserOccurrence();
        occurrence.setStart_time(data.getStartTime());
        occurrence.setEnd_time(data.getEndTime());
        occurrence.setAssignment(
                this.assignmentDAO.getFromCourseWithId(
                        data.getCourseId(),
                        data.getCourseYear(),
                        data.getAssignmentId()
                )
        );

        this.userDAO.getFromUUID(userId).addOccurrence(occurrence);

        return UserOccurrenceResponse.from(occurrence);
    }

    @PUT
    public UserOccurrenceResponse update(@PathParam("userId") String userId,
                                         OccurrenceData data) {
        User user = this.userDAO.getFromUUID(userId);
        List<UserOccurrence> occurrences = user.getOccurrences().stream().filter((o) -> {
            Assignment assignment = o.getAssignment();
            CourseEdition courseEdition = assignment.getCourse().getEdition();

            return assignment.getId() == data.getAssignmentId()
                    && courseEdition.getYear() == data.getCourseYear()
                    && courseEdition.getCourseId().equals(data.getCourseId());
        }).collect(Collectors.toList());
        
        if (occurrences.isEmpty()) {
            throw new IllegalArgumentException("Occurrence not found.");
        }

        UserOccurrence occurrence = occurrences.get(0);
        occurrence.setEnd_time(data.getEndTime());
        occurrence.setStart_time(data.getStartTime());

        this.userDAO.merge(user);

        return UserOccurrenceResponse.from(occurrence);
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
