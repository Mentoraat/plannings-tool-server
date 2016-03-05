package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.courses.QCourse.course;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.EntityManager;
import java.util.List;

public class CourseDAO extends AbstractDAO<Course> {

    @Inject
    protected CourseDAO(EntityManager entityManager) {
        super(entityManager);
    }

    @Transactional
    public Course getFromEdition(String courseId, int year) {
        return this.ensureExists(this.query().from(course)
                .where(course.edition.year.eq(year)
                        .and(course.edition.courseId.eq(courseId)))
                .singleResult(course));
    }

    @Transactional
    public List<Course> getAll() {
        return this.query()
                .from(course)
                .list(course);

    }

    @Transactional
    public Course getFromCourseCode(String courseName, int year) {
        return this.ensureExists(this.query().from(course)
                .where(course.edition.year.eq(year)
                        .and(course.courseName.eq(courseName))
                ).singleResult(course)
        );
    }

    @Transactional
    public boolean courseExists(String courseName, int year) {
        return this.query().from(course)
                .where(course.edition.year.eq(year)
                        .and(course.courseName.eq(courseName))
                ).singleResult(course) != null;
    }

    @Transactional
    public Course getFromUUID(String courseId) {
        return ensureExists(this.query().from(course)
                .where(course.uuid.eq(courseId))
                .singleResult(course));
    }
}
