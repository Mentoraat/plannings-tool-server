package nl.tudelft.planningstool.database.entities.assignments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.*;

@Data
@Entity
@Table(name = "assignments")
@EqualsAndHashCode(of = {
        "course", "id"
})
public class Assignment {

    public static final long DEFAULT_DEADLINE = -1L;

    public static final int DEFAULT_LENGTH = 2;

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "courseId"),
            @JoinColumn(name = "year")
    })
    private Course course;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "length", nullable = false)
    private double length = DEFAULT_LENGTH;

    @Column(name = "deadline")
    private long deadline = DEFAULT_DEADLINE;

    @Column(name = "description")
    private String description = "";

    public void setCourse(Course course) {
        this.course = course;

        if (Long.compare(this.getDeadline(), DEFAULT_DEADLINE) == 0) {
            this.setDeadline(course.getExamTime());
        }
    }
}
