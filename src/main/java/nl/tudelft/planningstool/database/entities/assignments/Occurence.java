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
@IdClass(Occurence.OccurenceId.class)
public class Occurence implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "assignment")
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
