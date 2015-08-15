package nl.tudelft.planningstool.database.controllers;

import static nl.tudelft.planningstool.database.entities.courses.QCourse.course;

import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.EntityManager;

public class CourseDAO extends AbstractDAO<Course> {

    protected CourseDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public Course getFromEdition(String courseId, int year) {
        return this.ensureExists(this.query().from(course)
                .where(course.edition.year.eq(year)
                        .and(course.edition.courseId.eq(courseId)))
                .singleResult(course));
    }
}
