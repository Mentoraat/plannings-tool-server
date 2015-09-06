package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.courses.Course;

import java.util.UUID;

@Data
public class AssignmentResponse {

    private Integer id;

    private AssignmentCourseResponse course;

    private String name;

    private double length;

    private long deadline;

    private String description;

    public static AssignmentResponse from(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();

        response.setId(assignment.getId());
        response.setName(assignment.getName());
        response.setCourse(AssignmentCourseResponse.from(assignment.getCourse()));
        response.setLength(assignment.getLength());
        response.setDeadline(assignment.getDeadline());
        response.setDescription(assignment.getDescription());

        return response;
    }

    @Data
    public static class AssignmentCourseResponse {

        private CourseEditionResponse edition;

        private UUID uuid;

        private long examTime;

        public static AssignmentCourseResponse from(Course course) {
            AssignmentCourseResponse response = new AssignmentCourseResponse();

            response.setUuid(course.getUuid());
            response.setEdition(CourseEditionResponse.from(course.getEdition()));
            response.setExamTime(course.getExamTime());

            return response;
        }
    }
}
