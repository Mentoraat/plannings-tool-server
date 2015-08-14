package nl.tudelft.planningstool.database.entities.courses;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class CourseEdition implements Serializable {

    @Column(name = "courseId", nullable = false)
    private String courseId;

    @Column(name = "year", nullable = false)
    private int year;
}
