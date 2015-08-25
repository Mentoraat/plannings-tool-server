package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.AssignmentDAO;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.OccurrenceDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;
import org.junit.Test;
import util.TestBase;

import javax.persistence.EntityExistsException;

import static org.junit.Assert.assertNotNull;

public class OccurrenceCreationTest extends TestBase {

    @Inject
    private OccurrenceDAO occurrenceDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private CourseDAO courseDAO;

    @Inject
    private AssignmentDAO assignmentDAO;

    @Test
    @TestBootstrap("courses/occurrences/no_occurrences.json")
    public void can_persist_occurrence() {
        Occurrence occurrence = new Occurrence();
        occurrence.setAssignment(this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1));
        occurrence.setStartingAt(1205);
        occurrence.setLength(5);
        occurrence.setUser(this.userDAO.getFromId(1));

        this.occurrenceDAO.persist(occurrence);
    }

    @Test
    @TestBootstrap("courses/occurrences/one_occurrence.json")
    public void can_retrieve_occurrence() {
        Assignment assignment = this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1);
        User user = this.userDAO.getFromId(1);
        Occurrence occurrence = this.occurrenceDAO.getOccurrenceForUser(user, assignment);

        assertNotNull(occurrence);
    }

    @Test
    @TestBootstrap("courses/occurrences/one_occurrence.json")
    public void can_not_persist_occurrence_for_same_course() {
        Occurrence occurrence = new Occurrence();
        occurrence.setAssignment(this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1));
        occurrence.setStartingAt(1205);
        occurrence.setLength(5);
        occurrence.setUser(this.userDAO.getFromId(1));
        occurrence.setId(1);

        expected.expect(EntityExistsException.class);
        this.occurrenceDAO.persist(occurrence);
    }

    @Test
    @TestBootstrap("courses/occurrences/two_courses_one_occurrence.json")
    public void can_persist_occurrence_for_different_course() {
        Occurrence occurrence = new Occurrence();
        occurrence.setAssignment(this.assignmentDAO.getFromCourseWithId("TI1505", 2015, 1));
        occurrence.setStartingAt(1205);
        occurrence.setLength(5);
        occurrence.setUser(this.userDAO.getFromId(1));

        this.occurrenceDAO.persist(occurrence);
    }
}
