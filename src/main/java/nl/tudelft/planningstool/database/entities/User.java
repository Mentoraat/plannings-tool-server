package nl.tudelft.planningstool.database.entities;

import javax.persistence.*;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

    /**
     * SoundCloud OAuth access token.
     * See: https://developers.soundcloud.com/docs/api/reference#token
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "access_token", nullable = true)
    private String accessToken;

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
