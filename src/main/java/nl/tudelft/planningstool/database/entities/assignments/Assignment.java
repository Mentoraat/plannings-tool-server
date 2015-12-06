package nl.tudelft.planningstool.database.entities.assignments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.planningstool.database.entities.courses.Course;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Table(name = "assignments")
@EqualsAndHashCode(of = {
        "course", "id"
})
@IdClass(Assignment.AssignmentId.class)
public class Assignment implements Serializable {

    public static final int DEFAULT_ID = -1;

    public static final long DEFAULT_DEADLINE = -1L;

    public static final int DEFAULT_LENGTH = 2;

    @Id
    @Column(name = "id", nullable = false)
    private Integer id = DEFAULT_ID;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "courseId"),
            @JoinColumn(name = "year")
    })
    private Course course;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "length", nullable = false)
    private double length = DEFAULT_LENGTH;

    @Column(name = "deadline")
    private long deadline = DEFAULT_DEADLINE;

    @Column(name = "description")
    private String description = "";

    public void setId(Integer id) {
        if (!this.getId().equals(DEFAULT_ID)) {
            throw new IllegalStateException("You are not allowed to redefine the id");
        }

        this.id = id;
    }

    public void setCourse(Course course) {
        this.course = course;

        if (Long.compare(this.getDeadline(), DEFAULT_DEADLINE) == 0) {
            this.setDeadline(course.getExamTime());
        }
    }

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyww");

    public void setDeadlineAsWeek(int week) throws ParseException {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        if (cal.get(Calendar.WEEK_OF_YEAR) > week) {
            year++;
        }

        Date date;

        synchronized (FORMAT) {
            date = FORMAT.parse(String.valueOf(year) + String.valueOf(week));
        }

        this.setDeadline(date.getTime());
    }

    @Data
    static class AssignmentId implements Serializable {

        private Integer id;

        private Course course;

    }
}
