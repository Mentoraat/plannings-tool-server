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
    public void can_persist_course() {
        Course course = new Course();

        CourseEdition edition = new CourseEdition();
        edition.setCourseId("TI1405");
        edition.setYear(2015);

        course.setEdition(edition);

        course.setExamTime(System.currentTimeMillis());

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

    }

}
