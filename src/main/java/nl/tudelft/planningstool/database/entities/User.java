package nl.tudelft.planningstool.database.entities;

import javax.persistence.*;
import javax.ws.rs.ForbiddenException;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = {
        "id"
})
@ToString(of = {
        "id", "name"
})
public class User implements AdminVerifiable {

    /**
     * The unique id of the user.
     *
     * @param id
     *            The new Id to set.
     * @return The id of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * The name of this user.
     *
     * @param name
     *            The name to set.
     * @return The name of this user.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "access_token", nullable = true, unique = true)
    private String accessToken;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @OneToMany(mappedBy = "user")
    private Set<CourseRelation> courses = Sets.newHashSet();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Occurrence> occurrences = Sets.newHashSet();

    @Enumerated(EnumType.STRING)
    @Column(name = "adminStatus")
    private AdminStatus adminStatus = AdminStatus.USER;

    public void addCourseRelation(CourseRelation relation) {
        relation.setUser(this);
        this.getCourses().add(relation);
        relation.getCourse().getUsers().add(relation);
    }

    @Override
    public void checkAdmin() throws ForbiddenException {
        this.adminStatus.checkAdmin();
    }

    public void addOccurrence(Occurrence occurrence) {
        occurrence.setUser(this);

        if (this.getOccurrences().contains(occurrence)) {
            throw new IllegalArgumentException(String.format(
                    "Occurrence {} already exists for user {}",
                    occurrence,
                    this));
        }

        this.getOccurrences().add(occurrence);
    }

    enum AdminStatus implements AdminVerifiable {

        ADMIN {
            @Override
            public void checkAdmin() throws ForbiddenException {

            }
        },
        USER {
            @Override
            public void checkAdmin() throws ForbiddenException {
                throw new ForbiddenException();
            }
        };

    }

}
