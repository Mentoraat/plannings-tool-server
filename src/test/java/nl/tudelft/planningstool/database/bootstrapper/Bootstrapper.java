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
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseEdition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        private String username;

        private int soundCloudUserId;

        private Integer points;
        
        private String accessToken;

    }

    @Data
    private static class BCourse {

        private BCourseEdition edition;

        private Set<BUser> users;

        private Set<BAssignment> assignments;

        private long examTime;

    }

    @Data
    private static class BCourseEdition {

        private String courseId;

        private int year;
    }

    @Data
    private static class BAssignment {

        private int id;

        private BCourse course;

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
        user.setId(bUser.getId());
        user.setName(bUser.getUsername());
        user.setAccessToken(bUser.getAccessToken());
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

        course.setExamTime(bCourse.getExamTime());

        checkForNull(bCourse.getAssignments(), this::createAssignment).forEach(course::addAssignment);

        Course persistedCourse = courseDAO.merge(course);
        log.info("Bootstrapper created course {}", persistedCourse);
    }

    protected Assignment createAssignment(BAssignment bAssignment) {
        final Assignment assignment = new Assignment();
        assignment.setDeadline(bAssignment.getDeadline());
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

}
