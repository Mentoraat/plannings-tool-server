package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.DatabaseTestModule;
import nl.tudelft.planningstool.database.DbModule;
import nl.tudelft.planningstool.database.bootstrapper.BootstrapRule;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseEdition;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.persistence.EntityExistsException;

import static org.junit.Assert.assertNotNull;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class CourseCreationTest {

    @Rule
    @Inject
    public BootstrapRule bootstrapRule;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Inject
    private CourseDAO courseDAO;

    @Test
    @TestBootstrap("no_course.json")
    public void can_persist_course() {
        Course course = createCourse("TI1405", 2015);

        this.courseDAO.persist(course);
    }

    @Test
    @TestBootstrap("one_course.json")
    public void can_retrieve_course() {
        Course course = this.courseDAO.getFromEdition("TI1405", 2015);

        assertNotNull(course);
    }

    @Test
    @TestBootstrap("one_course.json")
    public void can_not_persist_course_with_same_edition() {
        expected.expect(EntityExistsException.class);
        this.courseDAO.persist(createCourse("TI1405", 2015));
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
