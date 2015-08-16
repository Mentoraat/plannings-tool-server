package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.assignments.QOccurrence.occurrence;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;

import javax.persistence.EntityManager;

public class OccurrenceDAO extends AbstractDAO<Occurrence> {

    @Inject
    protected OccurrenceDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public Occurrence getOccurrenceForUser(User user, Assignment assignment) {
        return this.ensureExists(this.query().from(occurrence)
                .where(occurrence.user.eq(user)
                        .and(occurrence.assignment.eq(assignment)))
                .singleResult(occurrence));
    }
}
