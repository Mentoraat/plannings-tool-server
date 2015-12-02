package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Maps;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.security.Secured;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * API end-point to provide assignments for an user.
 */
@Path("v1/users/USER-{userId: (\\d|\\w|-)+}/courses/assignments")
public class UserAssignmentAPI extends ResponseAPI {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    static {
        TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("Amsterdam"));
    }

    /**
     * Get all the assignments for the provided user.
     * Returns only the assignments that have not been planned yet (e.g. do not have an occurrence relation with
     * the provided user).
     *
     * @param userId The id of the user.
     * @return A list of assigments.
     */
    @GET
    @Secured
    public ListResponse<AssignmentResponse> get(@PathParam("userId") String userId) {
        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> alreadyPlannedAssignments = user.getOccurrences().stream()
                .map(UserOccurrence::getAssignment)
                .collect(Collectors.toSet());

        List<Assignment> assignments = user.getCourses().stream()
                .map((c) -> c.getCourse().getAssignments())
                .flatMap(Collection::stream)
                .filter(a -> !alreadyPlannedAssignments.contains(a))
                .sorted((one, other) -> Long.compare(one.getDeadline(), other.getDeadline()))
                .collect(Collectors.toList());

        return createListResponse(assignments);
    }

    @GET
    @Path("/stats")
    public int[] getWithStatus(@PathParam("userId") String userId) {
        Map<UserOccurrence.OccurrenceStatus, AtomicInteger> map = Maps.newConcurrentMap();

        for (UserOccurrence.OccurrenceStatus status : UserOccurrence.OccurrenceStatus.values()) {
            map.put(status, new AtomicInteger());
        }

        User user = this.userDAO.getFromUUID(userId);
        user.getOccurrences().forEach(o -> {
            map.get(o.getStatus()).incrementAndGet();
        });

        long total = user.getCourses().stream()
                .map(CourseRelation::getCourse)
                .map(Course::getAssignments)
                .flatMap(Collection::stream)
                .count();

        int finished = map.get(UserOccurrence.OccurrenceStatus.FINISHED).get();
        return new int[] {finished, (int) total - finished};
    }

    @POST
    @Consumes("multipart/form-data")
    public String uploadAssignments(@PathParam("userId") String userId,
                                    MultipartFormDataInput input) {
        List<InputPart> inputParts = input.getFormDataMap().get("file");

        User user = this.userDAO.getFromUUID(userId);
        for (InputPart part : inputParts) {
            try {
                String fileContent = part.getBodyAsString();
                Scanner sc = new Scanner(fileContent);

                // Each week
                while (sc.hasNextLine()) {
                    // Start of the week
                    sc.nextLine();
                    // Headers
                    sc.nextLine();

                    String s;
                    // Weeks are split by an empty line
                    while (sc.hasNextLine() && !(s = sc.nextLine()).equals("")) {
                        createOccurrenceFromLine(s);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("No file attached");
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid file format");
            }
        }

        return "Successfully uploaded";
    }

    private void createOccurrenceFromLine(String s) throws ParseException {
        String[] parts = s.split(";");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\"", "");
        }

        String courseId = parts[0];
        String day = parts[4];
        String startTimeString = parts[5];
        String[] duration = parts[9].split(":");

        long startTime = TIME_FORMATTER.parse(day + "-" + startTimeString).getTime();
        double durationLong = Double.valueOf(duration[0]) + (Double.valueOf(duration[1]) / 60.0);

        CourseOccurrence o = new CourseOccurrence();
        o.plan(startTime, durationLong);

        // TODO: Handle not-found course
        // TODO: Specify how to find year
        Course course = this.courseDAO.getFromEdition(courseId, Integer.valueOf(day.split("-")[0]));
        course.addOccurrence(o);
        this.courseDAO.merge(course);
    }

    private ListResponse<AssignmentResponse> createListResponse(Collection<Assignment> assignments) {
        return createListResponse(assignments, AssignmentResponse::from);
    }
}
