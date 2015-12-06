package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Lists;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.security.Secured;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
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

        CourseRelation relation = u.getCourses().stream().filter(r -> {
            CourseEdition edition = r.getCourse().getEdition();

            return edition.getCourseId().equals(courseId) && edition.getYear() == year;
        }).findAny().get();

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

        response.setCourse(AssignmentResponse.AssignmentCourseResponse.from(course));
        response.setId(assignment.getId());

        return response;
    }
}
