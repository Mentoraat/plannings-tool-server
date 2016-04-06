package nl.tudelft.planningstool.api.v1;

import nl.tudelft.planningstool.api.responses.CourseResponse;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.api.security.Secured;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("v1/users/USER-{userId: .+}/courses/{courseId: .+}-{year: \\d+}/assignments")
@Secured
public class CourseAssignmentAPI extends ResponseAPI {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public AssignmentResponse create(@PathParam("userId") String userId,
                                     @PathParam("courseId") String courseId,
                                     @PathParam("year") int year,
                                     AssignmentResponse response) {
        User u = this.userDAO.getFromUUID(userId);

        Optional<CourseRelation> oRelation = u.getCourses().stream().filter(r -> {
            CourseEdition edition = r.getCourse().getEdition();

            return edition.getCourseId().equals(courseId) && edition.getYear() == year;
        }).findAny();

        if (!oRelation.isPresent()) {
            throw new IllegalArgumentException("Invalid course edition");
        }

        CourseRelation relation = oRelation.get();

        if (relation.getCourseRole() == CourseRelation.CourseRole.STUDENT) {
            u.checkAdmin();
        }

        final Course course = relation.getCourse();

        Assignment assignment = new Assignment();
        try {
            assignment.setDeadlineAsWeek((int) response.getDeadline());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid deadline format.");
        }
        assignment.setDescription(response.getDescription());
        assignment.setLength(response.getLength());
        assignment.setName(response.getName());

        course.addAssignment(assignment);

        this.courseDAO.merge(course);

        response.setCourse(CourseResponse.from(course));
        response.setId(assignment.getId());

        return response;
    }


    /**
     * Update an existing assignment.
     *
     * @param userId The id of the user.
     * @param data The data provided to update the assignment.
     * @return The assignment, if succesfully updated.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public AssignmentResponse update(@PathParam("userId") String userId,
                                     @PathParam("courseId") String courseId,
                                     @PathParam("year") int year,
                                     AssignmentResponse data) {

        Assignment assignment = this.assignmentDAO.getFromCourseWithId(courseId, year, data.getId());
        User user = this.userDAO.getFromUUID(userId);

        CourseRelation userRel = user.getCourses().stream()
                .filter(o ->
                        o.getCourse().getEdition().getCourseId().equals(courseId)
                        && o.getCourse().getEdition().getYear() == year)
                .findFirst()
                .get();

        if(userRel.getCourseRole() != CourseRelation.CourseRole.TEACHER) {
            throw new ForbiddenException("You may not edit this assignment");
        }

        assignment.setDeadline(data.getDeadline());
        assignment.setDescription(data.getDescription());
        assignment.setLength(data.getLength());
        assignment.setName(data.getName());

        this.assignmentDAO.merge(assignment);
        this.userDAO.merge(user);
        return data;
    }
}
