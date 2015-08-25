package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseEdition;
import org.junit.Test;
import util.TestBase;

import javax.persistence.EntityExistsException;

import static org.junit.Assert.assertNotNull;

public class CourseCreationTest extends TestBase {

    @Inject
    private CourseDAO courseDAO;

    @Test
    @TestBootstrap("courses/no_course.json")
    public void can_persist_course() {
        Course course = createCourse("TI1405", 2015);

        this.courseDAO.persist(course);
    }

    @Test
    @TestBootstrap("courses/one_course.json")
    public void can_retrieve_course() {
        Course course = this.courseDAO.getFromEdition("TI1405", 2015);

        assertNotNull(course);
    }

    @Test
    @TestBootstrap("courses/one_course.json")
    public void can_not_persist_course_with_same_edition() {
        expected.expect(EntityExistsException.class);
        this.courseDAO.persist(createCourse("TI1405", 2015));
    }

    @Test
    @TestBootstrap("courses/one_course.json")
    public void can_persist_course_with_different_year() {
        this.courseDAO.persist(createCourse("TI1405", 2016));
    }

    @Test
    @TestBootstrap("courses/one_course.json")
    public void can_persist_course_with_different_courseId() {
        this.courseDAO.persist(createCourse("TI1406", 2015));
    }

    private Course createCourse(String courseId, int year) {
        Course course = new Course();

        CourseEdition edition = new CourseEdition();
        edition.setCourseId(courseId);
        edition.setYear(year);

        course.setEdition(edition);

        course.setExamTime(System.currentTimeMillis());
        return course;
    }

}
