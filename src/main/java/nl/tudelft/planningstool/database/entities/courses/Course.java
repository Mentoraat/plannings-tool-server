package nl.tudelft.planningstool.database.entities.courses;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "courses")
@EqualsAndHashCode(of = "edition")
@ToString(of = "edition")
public class Course {

    @EmbeddedId
    private CourseEdition edition;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseRelation> users = Sets.newHashSet();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Assignment> assignments = Sets.newHashSet();

    @Column(name = "examTime")
    private long examTime;

    public void addAssignment(Assignment assignment) {
        assignment.setId(this.getAssignments().size() + 1);
        assignment.setCourse(this);

        this.getAssignments().add(assignment);
    }

    public Assignment getAssignment(int assignmentId) {
        for (Assignment assignment : this.getAssignments()) {
            if (assignment.getId().equals(assignmentId)) {
                return assignment;
            }
        }

        throw new EntityNotFoundException(String.format(
                "Assignment with id {} does not exist for course {}",
                assignmentId,
                this));
    }
}
