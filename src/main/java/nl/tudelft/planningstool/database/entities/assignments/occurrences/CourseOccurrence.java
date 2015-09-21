package nl.tudelft.planningstool.database.entities.assignments.occurrences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "course_occurrences")
@EqualsAndHashCode(
        callSuper = true,
        of = "course"
)
@IdClass(CourseOccurrence.OccurrenceId.class)
public class CourseOccurrence extends Occurrence {

    @Id
    @Column(name = "id")
    private int id;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "courseId"),
            @JoinColumn(name = "year")
    })
    private Course course;

    @Override
    protected void validateEndTime(long end_time) {

    }

    @Data
    static class OccurrenceId implements Serializable {

        private int id;

        private Course course;

    }

}
