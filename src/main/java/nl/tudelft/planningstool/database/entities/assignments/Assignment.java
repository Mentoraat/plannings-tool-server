package nl.tudelft.planningstool.database.entities.assignments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "assignments")
@EqualsAndHashCode(of = {
        "course", "id"
})
@IdClass(Assignment.AssignmentId.class)
public class Assignment {

    public static final int DEFAULT_ID = -1;

    public static final long DEFAULT_DEADLINE = -1L;

    public static final int DEFAULT_LENGTH = 2;

    @Id
    @Column(name = "id", nullable = false)
    private Integer id = DEFAULT_ID;

    @Id
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

    public void setId(Integer id) {
        if (!this.getId().equals(DEFAULT_ID)) {
            throw new IllegalStateException("You are not allowed to redefine the id");
        }

        this.id = id;
    }

    public void setCourse(Course course) {
        this.course = course;

        if (Long.compare(this.getDeadline(), DEFAULT_DEADLINE) == 0) {
            this.setDeadline(course.getExamTime());
        }
    }

    @Data
    static class AssignmentId implements Serializable {

        private Integer id;

        private Course course;

    }
}
