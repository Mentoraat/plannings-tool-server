package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;

@Data
public class CourseEditionResponse implements Comparable<CourseEditionResponse> {

    private String courseId;

    private int year;

    private String courseName;

    public static CourseEditionResponse from(CourseEdition edition) {
        CourseEditionResponse response = new CourseEditionResponse();

        response.setCourseId(edition.getCourseId());
        response.setYear(edition.getYear());

        return response;
    }

    @Override
    public String toString() {
        return courseId + "," + year;
    }

    @Override
    public int compareTo(CourseEditionResponse that) {
        int i = Integer.compare(this.year, that.year);

        if (i != 0) {
            return i;
        }

        return this.courseId.compareTo(that.courseId);
    }
}
