package nl.tudelft.planningstool.api.responses.occurrences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.api.responses.CourseEditionResponse;
import nl.tudelft.planningstool.api.responses.CourseResponse;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;

import java.util.UUID;

/**
 * A {@link OccurrenceResponse} which is course-wide.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseOccurrenceResponse extends OccurrenceResponse {

    private CourseResponse course;

    public static CourseOccurrenceResponse from(CourseOccurrence occurrence) {
        CourseOccurrenceResponse response = new CourseOccurrenceResponse();

        response.setCourse(CourseResponse.from(occurrence.getCourse()));

        response.process(occurrence, false, "College");

        return response;
    }
}
