package nl.tudelft.planningstool.database.entities.courses;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "courses")
@EqualsAndHashCode(of = "edition")
@ToString(of = "edition")
public class Course implements Serializable {

    @EmbeddedId
    private CourseEdition edition;

    @Column(name = "courseName", nullable = true)
    private String courseName;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String uuid;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseRelation> users = Sets.newHashSet();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Assignment> assignments = Lists.newArrayList();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseOccurrence> occurrences = Sets.newHashSet();

    @Column(name = "examTime")
    private long examTime;

    public void addAssignment(Assignment assignment) {
        assignment.setId(this.getAssignments().size() + 1);
        assignment.setCourse(this);

        this.getAssignments().add(assignment);
    }

    public void addOccurrence(CourseOccurrence o) {
        o.setId(this.getOccurrences().size() + 1);
        o.setCourse(this);

        this.getOccurrences().add(o);
    }

    public Assignment getAssignment(int assignmentId) {
        for (Assignment assignment : this.getAssignments()) {
            if (assignment.getId().equals(assignmentId)) {
                return assignment;
            }
        }

        throw new EntityNotFoundException(String.format(
                "Assignment with id %s does not exist for course %s",
                assignmentId,
                this));
    }
}
