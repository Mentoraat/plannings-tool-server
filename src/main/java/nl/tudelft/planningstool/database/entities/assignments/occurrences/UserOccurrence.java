package nl.tudelft.planningstool.database.entities.assignments.occurrences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_occurrences")
@EqualsAndHashCode(
        callSuper = true,
        of = {
            "assignment", "user"
        }
)
@IdClass(UserOccurrence.OccurrenceId.class)
public class UserOccurrence extends Occurrence {

    @Id
    @Column(name = "id")
    private int id;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "assignmentId"),
            @JoinColumn(name = "courseId"),
            @JoinColumn(name = "year")
    })
    private Assignment assignment;

    @Id
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "occurrenceStatus")
    private OccurrenceStatus status = OccurrenceStatus.PLANNED;

    @Override
    protected void validateEndTime(long end_time) {
        if (end_time > this.getAssignment().getDeadline()) {
            throw new IllegalArgumentException("Assignment must be finished before the deadline.");
        }
    }

    @Data
    static class OccurrenceId implements Serializable {

        private int id;

        private Assignment assignment;

        private User user;

    }

    public enum OccurrenceStatus {
        PLANNED, UNFINISHED, FINISHED;
    }
}
