package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.courses.QCourse.course;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.EntityManager;
import java.util.UUID;

public class CourseDAO extends AbstractDAO<Course> {

    @Inject
    protected CourseDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public Course getFromEdition(String courseId, int year) {
        return this.ensureExists(this.query().from(course)
                .where(course.edition.year.eq(year)
                        .and(course.edition.courseId.eq(courseId)))
                .singleResult(course));
    }

    public Course getFromUUID(UUID courseId) {
        return ensureExists(this.query().from(course)
                .where(course.uuid.eq(courseId))
                .singleResult(course));
    }

    public Course getFromUUID(String courseId) {
        return getFromUUID(UUID.fromString(courseId));
    }
}
