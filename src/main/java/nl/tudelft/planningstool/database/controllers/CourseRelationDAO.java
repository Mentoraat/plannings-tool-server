package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.courses.QCourseRelation.courseRelation;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.persistence.EntityManager;

public class CourseRelationDAO extends AbstractDAO<CourseRelation> {

    @Inject
    protected CourseRelationDAO(EntityManager entityManager) {
        super(entityManager);
    }

    @Transactional
    public CourseRelation getCourseRelationForUser(User user, Course course) {
        return this.ensureExists(this.query().from(courseRelation)
                .where(courseRelation.user.eq(user)
                        .and(courseRelation.course.eq(course)))
                .singleResult(courseRelation));
    }
}
