package nl.tudelft.planningstool.database.bootstrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.planningstool.database.controllers.CourseDAO;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.embeddables.CourseEdition;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@code Bootstrapper} loads an environment for a test
 *
 * @author Jan-Willem Gmelig Meyling
 */
@Slf4j
public class Bootstrapper {

    private final Map<Integer, User> persistedUsers;

    /**
     * ObjectMapper used for the bootstrapper.
     */
    private final ObjectMapper objectMapper;

    private final UserDAO userDAO;

    private final CourseDAO courseDAO;

    @Inject
    public Bootstrapper(final ObjectMapper objectMapper, final UserDAO userDAO, final CourseDAO courseDAO) {
        this.objectMapper = objectMapper;
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
        this.persistedUsers = Maps.newHashMap();
    }

    /**
     * BEnvironment contains the artists and songs to be initialised.
     */
    @Data
    private static class BEnvironment {

        private List<BUser> users;

        private List<BCourse> courses;

    }

    @Data
    private static class BUser {

        private int id;

        private String name;

        private String accessToken;

        private String uuid;

        private String status;

        private String hashedPassword;

        private String email;

        private int studentNumber;

    }

    @Data
    private static class BCourse {

        private BCourseEdition edition;

        private Set<BCourseRelation> users;

        private List<BAssignment> assignments;

        private Set<BOccurrence> occurrences;

        private long examTime;

        private UUID uuid;

        public void setUuid(String uuidString) {
            uuid = UUID.fromString(uuidString);
        }

    }

    @Data
    private static class BCourseRelation {

        private int id;

        private String role;

        private Set<BUserOccurrence> occurrences;
    }

    @Data
    private static class BOccurrence {

        private int id;

        private long startingAt;

        private double length;
    }

    @Data
    private static class BUserOccurrence extends BOccurrence {

        private int assignment;

        private String status;
    }

    @Data
    private static class BCourseEdition {

        private String courseId;

        private int year;

        private String courseName = "";
    }

    @Data
    private static class BAssignment {

        private String name;

        private double length = Assignment.DEFAULT_LENGTH;

        private long deadline = Assignment.DEFAULT_DEADLINE;

        private String description = "";
    }

    /**
     * Parse environment from resource file.
     *
     * @param path
     *            path to resource
     * @throws IOException
     *             if an I/O error occurs
     */
    public void parseFromResource(final String path) throws IOException {
        try (InputStream in = Bootstrapper.class.getResourceAsStream(File.separatorChar + "bootstrap" + File.separatorChar + path)) {
            final BEnvironment environment = objectMapper.readValue(in, BEnvironment.class);

            checkForNull(environment.getUsers(), this::createUser);
            checkForNull(environment.getCourses(), this::createCourse);
        }
    }

    private <T> void checkForNull(List<T> list, Consumer<T> consumer) {
        if (list != null) {
            list.forEach(consumer);
        }
    }

    private <T, R> List<R> checkForNull(List<T> list, Function<T, R> function) {
        if (list != null) {
            return list.stream().map(function).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    private <T, R> Set<R> checkForNull(Set<T> set, Function<T, R> function) {
        if (set != null) {
            return set.stream().map(function).collect(Collectors.toSet());
        }

        return Sets.newHashSet();
    }

    @Transactional
    protected void createUser(BUser bUser) {
        final User user = new User();
        user.setName(bUser.getName());
        user.setAccessToken(bUser.getAccessToken());
        user.setUuid(bUser.getUuid());
        user.setHashedPassword(bUser.getHashedPassword());
        user.setEmail(bUser.getEmail());
        user.setStudentNumber(bUser.getStudentNumber());

        if (bUser.getStatus() != null) {
            user.setAdminStatus(User.AdminStatus.valueOf(bUser.getStatus()));
        }

        User persistedUser = userDAO.merge(user);
        persistedUsers.put(persistedUser.getId(), persistedUser);
        log.info("Bootstrapper created user {}", persistedUser);
    }

    @Transactional
    protected void createCourse(BCourse bCourse) {
        final Course course = new Course();

        final CourseEdition edition = new CourseEdition();
        edition.setCourseId(bCourse.getEdition().getCourseId());
        edition.setYear(bCourse.getEdition().getYear());
        course.setEdition(edition);

        course.setCourseName(bCourse.getEdition().getCourseName());
        course.setExamTime(getUnixFromHour(bCourse.getExamTime()));

        if (bCourse.getUuid() != null) {
            course.setUuid(bCourse.getUuid().toString());
        }

        course.setOccurrences(checkForNull(bCourse.getOccurrences(), (o) -> createCourseOccurrence(o, course)));

        checkForNull(bCourse.getAssignments(), (a) -> { return this.createAssignment(a, course.getExamTime()); }).forEach(course::addAssignment);

        Course persistedCourse = courseDAO.merge(course);
        persistCourseUsers(persistedCourse, bCourse.getUsers());
        courseDAO.merge(persistedCourse);

        log.info("Bootstrapper created course {}", persistedCourse);
    }

    private CourseOccurrence createCourseOccurrence(BOccurrence occurrence, Course course) {
        CourseOccurrence courseOccurrence = new CourseOccurrence();

        courseOccurrence.plan(getUnixFromHour(occurrence.getStartingAt()), occurrence.getLength());
        courseOccurrence.setCourse(course);
        courseOccurrence.setId(occurrence.getId());

        return courseOccurrence;
    }

    private void persistCourseUsers(Course course, Set<BCourseRelation> users) {
        users.forEach((u) -> {
            CourseRelation relation = new CourseRelation();

            relation.setCourse(course);
            relation.setCourseRole(CourseRelation.CourseRole.valueOf(u.getRole()));

            User user = persistedUsers.get(u.getId());
            user.addCourseRelation(relation);

            if (u.getOccurrences() != null) {
                persistOccurrences(course, user, u.getOccurrences());
            }
        });
    }

    private void persistOccurrences(Course course, User user, Set<BUserOccurrence> occurrences) {
        occurrences.forEach((o) -> {
            UserOccurrence occurrence = new UserOccurrence();
            occurrence.setAssignment(course.getAssignment(o.getAssignment()));
            occurrence.plan(this.getUnixFromHour(o.getStartingAt()), o.getLength());
            occurrence.setId(o.getId());
            occurrence.setUser(user);

            if (o.getStatus() != null) {
                occurrence.setStatus(UserOccurrence.OccurrenceStatus.valueOf(o.getStatus()));
            }

            user.addOccurrence(occurrence);
        });
    }

    protected Assignment createAssignment(BAssignment bAssignment, long examTime) {
        final Assignment assignment = new Assignment();
        assignment.setDeadline(examTime);
        assignment.setDescription(bAssignment.getDescription());
        assignment.setLength(bAssignment.getLength());
        assignment.setName(bAssignment.getName());

        return assignment;
    }

    /**
     * Remove persisted entitites from the database.
     */
    public void cleanup() {
    }
    
    public User getUser(Integer id) {
        return persistedUsers.get(id);
    }

    private long getUnixFromHour(long hours) {
        return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hours);
    }

}
