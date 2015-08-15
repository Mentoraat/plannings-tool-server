package nl.tudelft.planningstool.database.entities;

import javax.persistence.*;
import javax.ws.rs.ForbiddenException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = {
        "id"
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

    @OneToMany(mappedBy = "user")
    private Set<CourseRelation> courses;

    @Enumerated(EnumType.STRING)
    @Column(name = "adminStatus")
    private AdminStatus adminStatus = AdminStatus.USER;

    @Override
    public void checkAdmin() throws ForbiddenException {
        this.adminStatus.checkAdmin();
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
