package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.assignments.occurrences.QUserOccurrence.userOccurrence;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;

import javax.persistence.EntityManager;

public class OccurrenceDAO extends AbstractDAO<Occurrence> {

    @Inject
    protected OccurrenceDAO(EntityManager entityManager) {
        super(entityManager);
    }

    @Transactional
    public Occurrence getOccurrenceForUser(User user, Assignment assignment) {
        return this.ensureExists(this.query().from(userOccurrence)
                .where(userOccurrence.user.eq(user)
                        .and(userOccurrence.assignment.eq(assignment)))
                .singleResult(userOccurrence));
    }
}
