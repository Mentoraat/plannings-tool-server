package nl.tudelft.planningstool.database.entities.assignments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.User;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "occurrences")
@EqualsAndHashCode(of = {
        "assignment", "user"
})
@IdClass(Occurrence.OccurenceId.class)
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

    @Data
    static class OccurenceId implements Serializable {

        private Assignment assignment;

        private User user;

    }
}