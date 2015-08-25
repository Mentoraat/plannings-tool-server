package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.CourseRelationDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.junit.Test;

import javax.persistence.EntityExistsException;

public class CourseRelationCreationTest extends EntityCreationTestBase {

    @Inject
    private UserDAO userDAO;

    @Inject
    private CourseDAO courseDAO;

    @Inject
    private CourseRelationDAO courseRelationDAO;

    @Test
    @TestBootstrap("users/courserelations/no_courserelations.json")
    public void can_persist_courseRelation() {
        CourseRelation relation = new CourseRelation();
        relation.setCourse(this.courseDAO.getFromEdition("TI1405", 2015));
        relation.setUser(this.userDAO.getFromId(1));
        relation.setCourseRole(CourseRelation.CourseRole.TEACHER);

        this.courseRelationDAO.persist(relation);
    }

    @Test
    @TestBootstrap("users/courserelations/one_courserelation.json")
    public void can_not_persist_courseRelation_for_same_course() {
        CourseRelation relation = new CourseRelation();
        relation.setCourse(this.courseDAO.getFromEdition("TI1405", 2015));
        relation.setUser(this.userDAO.getFromId(1));
        relation.setCourseRole(CourseRelation.CourseRole.TEACHER);

        expected.expect(EntityExistsException.class);
        this.courseRelationDAO.persist(relation);
    }

    @Test
    @TestBootstrap("users/courserelations/two_users_one_courserelation.json")
    public void can_persist_courseRelation_for_same_course_different_user() {
        CourseRelation relation = new CourseRelation();
        relation.setCourse(this.courseDAO.getFromEdition("TI1405", 2015));
        relation.setUser(this.userDAO.getFromId(2));
        relation.setCourseRole(CourseRelation.CourseRole.TEACHER);

        this.courseRelationDAO.persist(relation);
    }
}
