package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.CourseEditionResponse;
import nl.tudelft.planningstool.api.responses.CourseResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.CourseOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.api.security.Secured;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * API end-point to provide occurrences for an user.
 */
@Slf4j
@Path("v1/users/USER-{userId: .+}/occurrences")
@Secured
public class UserOccurrenceAPI extends ResponseAPI {

    private static final List<String> COLORS = Lists.newArrayList("#ff6447", "#5441b0", "#708090");

    /**
     * Get all occurrences for the user in the given timeslot. Aggregrates personal occurrences as well as course-wide
     * occurrences.
     *
     * @param userId The id of the user.
     * @param timeSlot The timeslot the occurrence must happen in.
     * @return All occurrences that happen for the user in the timeslot.
     */
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

    /**
     * Create a new occurrence for the provided user.
     *
     * @param userId The id of the user.
     * @param data The data to provided to create the occurrence from.
     * @return The Occurrence, if succesfully created.
     */
    @POST
    public UserOccurrenceResponse create(@PathParam("userId") String userId,
                                         UserOccurrenceResponse data) {
        User user = this.userDAO.getFromUUID(userId);

        UserOccurrence occurrence = new UserOccurrence();
        occurrence.setStart_time(data.getStartTime());
        occurrence.setEnd_time(data.getEndTime());
        occurrence.setActualLength(data.getActualLength());
        occurrence.setNotes(data.getNotes());

        AssignmentResponse assignment = data.getAssignment();
        checkAndCreateUserCourse(user, assignment);
        occurrence.setAssignment(
                this.assignmentDAO.getFromCourseWithId(
                        assignment.getCourse().getEdition().getCourseId(),
                        assignment.getCourse().getEdition().getYear(),
                        assignment.getId()
                )
        );

        user.addOccurrence(occurrence);

        log.info("Created occurrence {}", occurrence);

        this.userDAO.merge(user);

        return UserOccurrenceResponse.from(occurrence);
    }

    private void checkAndCreateUserCourse(User user, AssignmentResponse assignment) {
        String courseCode = "USER-" + user.getId();
        if (!assignment.getCourse().getUuid().equals(courseCode)) {
            return;
        }

        if (!this.courseDAO.courseExists(courseCode, 1)) {
            Course course = new Course();
            course.setCourseName(courseCode);
            CourseEdition edition = new CourseEdition();
            edition.setCourseId(courseCode);
            edition.setYear(1);
            course.setEdition(edition);

            assignment.setCourse(CourseResponse.from(this.courseDAO.persist(course)));

            CourseRelation relation = new CourseRelation();
            relation.setCourse(course);
            relation.setCourseRole(CourseRelation.CourseRole.TEACHER);
            user.addCourseRelation(relation);
            this.courseDAO.merge(course);
            this.userDAO.merge(user);
        } else {
            assignment.setCourse(CourseResponse.from(this.courseDAO.getFromCourseCode(courseCode, 1)));
        }

        Assignment assignmentEntity = new Assignment();
        assignmentEntity.setName(assignment.getName());
        this.courseDAO.getFromCourseCode(courseCode, 1).addAssignment(assignmentEntity);
        assignment.setId(assignmentEntity.getId());
    }

    /**
     * Update an existing occurrence for the provided user.
     *
     * @param userId The id of the user.
     * @param data The data provided to update the occurrence.
     * @return The occurrence, if succesfully updated.
     */
    @PUT
    public UserOccurrenceResponse update(@PathParam("userId") String userId,
                                         UserOccurrenceResponse data) {
        User user = this.userDAO.getFromUUID(userId);
        AssignmentResponse assignmentData = data.getAssignment();
        int year = assignmentData.getCourse().getEdition().getYear();
        String courseId = assignmentData.getCourse().getEdition().getCourseId();

        List<UserOccurrence> occurrences = user.getOccurrences().stream().filter((o) -> {
            Assignment assignment = o.getAssignment();
            CourseEdition courseEdition = assignment.getCourse().getEdition();

            return assignment.getId().equals(assignmentData.getId())
                    && courseEdition.getYear() == year
                    && courseEdition.getCourseId().equals(courseId);
        }).collect(Collectors.toList());

        if (occurrences.isEmpty()) {
            throw new IllegalArgumentException("Occurrence not found.");
        }

        UserOccurrence occurrence = occurrences.get(0);
        occurrence.setEnd_time(data.getEndTime());
        occurrence.setStart_time(data.getStartTime());
        occurrence.setStatus(data.getStatus());
        occurrence.setActualLength(data.getActualLength());
        occurrence.setNotes(data.getNotes());

        log.info("Updated occurrence {}", occurrence);

        this.userDAO.merge(user);

        return data;
    }

    /**
     * Get all occurrences of a specific course for the provided user.
     *
     * @param userId The id of the user.
     * @param courseId The courseId of the course.
     * @return All occurrences of the course for the user.
     */
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

    @DELETE
    @Path("/courses/COURSE-{courseId: (\\d|\\w|-)+}/{assignmentId: (\\d)+}")
    public void deleteOccurrence(@PathParam("userId") String userId, @PathParam("courseId") String courseId, @PathParam("assignmentId") Integer assignmentId) {
        final Course course = this.courseDAO.getFromUUID(courseId);
        final User user = this.userDAO.getFromUUID(userId);
        final Predicate<UserOccurrence> predicate = o -> o.getAssignment().getCourse().equals(course) && o.getAssignment().getId().equals(assignmentId);

        user.getOccurrences().removeIf(predicate);
        this.userDAO.merge(user);
    }


    private ListResponse<UserOccurrenceResponse> createListResponse(Collection<UserOccurrence> occurrences) {
        return createListResponse(occurrences, UserOccurrenceResponse::from);
    }

}
