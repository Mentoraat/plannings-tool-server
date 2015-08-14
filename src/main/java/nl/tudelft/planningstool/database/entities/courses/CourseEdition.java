package nl.tudelft.planningstool.database.entities.courses;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class CourseEdition {

    @Column(name = "courseId", nullable = false)
    private String courseId;

    @Column(name = "year", nullable = false)
    private int year;
}
