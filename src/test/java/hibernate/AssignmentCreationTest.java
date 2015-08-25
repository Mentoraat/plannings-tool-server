package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.AssignmentDAO;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.courses.Course;
import org.junit.Test;
import util.TestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class AssignmentCreationTest extends TestBase {

    @Inject
    private CourseDAO courseDAO;

    @Inject
    private AssignmentDAO assignmentDAO;

    @Test
    @TestBootstrap("courses/assignments/no_assignments.json")
    public void can_add_assignment_to_course() {
        Course course = this.courseDAO.getFromEdition("TI1405", 2015);

        Assignment assignment = createAssignment("Assignment 1", "The first assignment!");

        course.addAssignment(assignment);

        this.courseDAO.merge(course);

        assertEquals(1, assignment.getId().intValue());
    }

    @Test
    @TestBootstrap("courses/assignments/one_assignment.json")
    public void can_retrieve_assignment() {
        Assignment assignment = this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1);

        assertNotNull(assignment);
    }

    @Test
    @TestBootstrap("courses/assignments/one_assignment.json")
    public void can_add_assignment_to_course_increments_index() {
        Course course = this.courseDAO.getFromEdition("TI1405", 2015);

        Assignment assignment = createAssignment("Assignment 2", "The second assignment!");

        course.addAssignment(assignment);

        this.courseDAO.merge(course);

        assertEquals(2, assignment.getId().intValue());
    }

    @Test
    @TestBootstrap("courses/assignments/two_courses_one_assignment.json")
    public void can_add_assignment_to_course_with_different_course_same_name() {
        Course course = this.courseDAO.getFromEdition("TI1505", 2015);

        Assignment assignment = createAssignment("Assignment 1", "The first assignment!");

        course.addAssignment(assignment);

        this.courseDAO.merge(course);

        assertEquals(1, assignment.getId().intValue());
        assertNotEquals(assignment, this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1));
    }

    @Test
    @TestBootstrap("courses/assignments/one_assignment.json")
    public void can_not_redefine_id() {
        Assignment assignment = this.assignmentDAO.getFromCourseWithId("TI1405", 2015, 1);

        expected.expect(IllegalStateException.class);
        assignment.setId(2);
    }

    private Assignment createAssignment(String name, String description) {
        Assignment assignment = new Assignment();
        assignment.setName(name);
        assignment.setDescription(description);
        assignment.setLength(3);
        assignment.setDeadline(System.currentTimeMillis());
        return assignment;
    }
}
