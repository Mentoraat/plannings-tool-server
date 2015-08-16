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
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id"),
            @JoinColumn(name = "courseId"),
            @JoinColumn(name = "year")
    })
    private Assignment assignment;

    @Id
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Column(name = "starting_at", nullable = false)
    private long startingAt;

    @Column(name = "length")
    private double length;

    public void setLength(double length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive and greater than zero.");
        }

        this.length = length;
    }

    public void plan(long startingAt, double length) {
        if (startingAt + TimeUnit.HOURS.toMillis(Double.doubleToLongBits(length)) > this.getAssignment().getDeadline()) {
            throw new IllegalArgumentException("Assignment must be finished before the deadline.");
        }

        this.setStartingAt(startingAt);
        this.setLength(length);
    }

    @Data
    static class OccurrenceId implements Serializable {

        private Assignment assignment;

        private User user;

    }
}
