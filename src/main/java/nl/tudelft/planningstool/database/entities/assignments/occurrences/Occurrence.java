package nl.tudelft.planningstool.database.entities.assignments.occurrences;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@MappedSuperclass
public abstract class Occurrence implements Serializable {

    @Column(name = "start_time", nullable = false)
    private long start_time;

    @Column(name = "end_time")
    private long end_time;

    public void plan(long start_time, double length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive and greater than zero.");
        }

        long end_time = calculateEnd_time(start_time, length);

        validateEndTime(end_time);

        this.setStart_time(start_time);
        this.setEnd_time(end_time);
    }

    public static long calculateEnd_time(long start_time, double length) {
        return start_time + TimeUnit.HOURS.toMillis((long) length);
    }

    protected abstract void validateEndTime(long end_time);
}
