package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;

@Data
public class CourseEditionResponse {

    private String courseId;

    private int year;


    public static CourseEditionResponse from(CourseEdition edition) {
        CourseEditionResponse response = new CourseEditionResponse();

        response.setCourseId(edition.getCourseId());
        response.setYear(edition.getYear());

        return response;
    }
}
