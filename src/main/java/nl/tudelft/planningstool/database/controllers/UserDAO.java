package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.QUser.user;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.entities.User;

import javax.persistence.EntityManager;
import java.util.UUID;

public class UserDAO extends AbstractDAO<User> {

    @Inject
    protected UserDAO(EntityManager entityManager) {
        super(entityManager);
    }


    public User getFromId(int i) {
        return ensureExists(this.query().from(user)
                .where(user.id.eq(i))
                .singleResult(user));
    }

    public User getFromUUID(UUID userId) {
        return ensureExists(this.query().from(user)
                .where(user.uuid.eq(userId))
                .singleResult(user));
    }
}
