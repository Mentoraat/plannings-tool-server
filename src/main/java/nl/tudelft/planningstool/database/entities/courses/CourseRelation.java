package nl.tudelft.planningstool.database.entities.courses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.User;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "course_relations")
@EqualsAndHashCode(of = {
        "course", "user"
})
public class CourseRelation implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "course")
    private Course course;

    @Id
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Column(name = "role")
    private CourseRole courseRole = CourseRole.STUDENT;

    enum CourseRole {
        TEACHER(),
        ASSISTANT(),
        STUDENT();
    }
}
