package nl.tudelft.planningstool.database.entities.assignments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@Entity
@Table(name = "occurrences")
@EqualsAndHashCode(of = {
        "assignment", "user"
})
@IdClass(Occurrence.OccurrenceId.class)
public class Occurrence implements Serializable {

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

    @Column(name = "start_time", nullable = false)
    private long start_time;

    @Column(name = "end_time")
    private long end_time;

    public void plan(long start_time, double length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive and greater than zero.");
        }

        long end_time = calculateEnd_time(start_time, length);

        if (end_time > this.getAssignment().getDeadline()) {
            throw new IllegalArgumentException("Assignment must be finished before the deadline.");
        }

        this.setStart_time(start_time);
        this.setEnd_time(end_time);
    }

    public static long calculateEnd_time(long start_time, double length) {
        return start_time + TimeUnit.HOURS.toSeconds(new Double(length).longValue());
    }

    @Data
    static class OccurrenceId implements Serializable {

        private int id;

        private Assignment assignment;

        private User user;

    }
}
