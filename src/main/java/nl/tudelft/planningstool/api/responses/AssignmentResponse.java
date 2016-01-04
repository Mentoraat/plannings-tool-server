package nl.tudelft.planningstool.api.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignmentResponse {

    private Integer id;

    private CourseResponse course;

    private String name;

    private double length;

    private long deadline;

    private String description;

    public static AssignmentResponse from(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();

        response.setId(assignment.getId());
        response.setName(assignment.getName());
        response.setCourse(CourseResponse.from(assignment.getCourse()));
        response.setLength(assignment.getLength());
        response.setDeadline(assignment.getDeadline());
        response.setDescription(assignment.getDescription());

        return response;
    }

}
