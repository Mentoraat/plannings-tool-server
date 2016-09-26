package nl.tudelft.planningstool.database.entities;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.ws.rs.ForbiddenException;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(columnList = "uuid", unique = true),
                @Index(columnList = "name", unique = true)
        })
@EqualsAndHashCode(of = {
        "id"
})
@ToString(of = {
        "id", "name"
})
public class User implements AdminVerifiable, Serializable {

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

    @Column(name = "hashed_password", nullable = false, unique = false)
    private String hashedPassword;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "studentnumber", nullable = false, unique = true)
    private Integer studentNumber;

    @OneToMany(mappedBy = "user")
    private Set<CourseRelation> courses = Sets.newHashSet();

    // http://stackoverflow.com/questions/549961/hibernate-removing-item-from-a-list-does-not-persist#comment12057916_550441
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserOccurrence> occurrences = Sets.newHashSet();

    @Enumerated(EnumType.STRING)
    @Column(name = "adminStatus")
    private AdminStatus adminStatus = AdminStatus.USER;

    @Column(name = "resetToken")
    private String resetToken;

    @Column(name = "resetTokenValidity")
    private long resetTokenValidity;

    public void addCourseRelation(CourseRelation relation) {
        relation.setUser(this);
        this.getCourses().add(relation);
        relation.getCourse().getUsers().add(relation);
    }

    @Override
    public void checkAdmin() throws ForbiddenException {
        this.adminStatus.checkAdmin();
    }

    public void addOccurrence(UserOccurrence occurrence) {
        occurrence.setUser(this);

        if (this.getOccurrences().contains(occurrence)) {
            throw new IllegalArgumentException(String.format(
                    "Occurrence %s already exists for user %s",
                    occurrence,
                    this));
        }

        this.getOccurrences().add(occurrence);
    }

    public enum AdminStatus implements AdminVerifiable {

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
