package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.courses.Course;

@Data
public class CourseResponse {

    private CourseEditionResponse edition;

    private String uuid;

    private long examTime;

    public static CourseResponse from(Course course) {
        CourseResponse response = new CourseResponse();

        response.setUuid(course.getUuid());
        response.setEdition(CourseEditionResponse.from(course.getEdition()));
        response.setExamTime(course.getExamTime());

        return response;
    }
}
