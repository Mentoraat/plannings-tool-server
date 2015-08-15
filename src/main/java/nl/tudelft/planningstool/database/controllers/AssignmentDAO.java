package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.assignments.QAssignment.assignment;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;

import javax.persistence.EntityManager;

public class AssignmentDAO extends AbstractDAO<Assignment> {

    @Inject
    protected AssignmentDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public Assignment getFromCourseWithId(String courseId, int year, int assignmentid) {
        return ensureExists(this.query().from(assignment)
                .where(assignment.course.edition.courseId.eq(courseId)
                        .and(assignment.course.edition.year.eq(year))
                        .and(assignment.id.eq(assignmentid)))
                .singleResult(assignment));
    }
}
