package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@Path("v1/users/USER-{userId: .+}/courses/assignments")
@Secured
public class UserAssignmentAPI extends ResponseAPI {

    /**
     * Get all the assignments for the provided user.
     * Returns only the assignments that have not been planned yet (e.g. do not have an occurrence relation with
     * the provided user).
     *
     * @param userId The id of the user.
     * @return A list of assigments.
     */
    @GET
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

    @Data
    @AllArgsConstructor
    private class CourseData {
        private String name;
        private double[] data;
    }

    @GET
    @Path("/stats")
    public List<CourseData> getWithStatus(@PathParam("userId") String userId) {
        User user = this.userDAO.getFromUUID(userId);

        Collection<Assignment> alreadyPlannedAssignments = user.getOccurrences().stream()
                .map(UserOccurrence::getAssignment)
                .collect(Collectors.toSet());

        return user.getCourses().stream()
                .map(CourseRelation::getCourse)
                .map(c -> new CourseData(c.getCourseName(), calculateBarDataForCourse(user, c, alreadyPlannedAssignments)))
                .collect(Collectors.toList());
    }

    private double[] calculateBarDataForCourse(User user, Course c, Collection<Assignment> alreadyPlannedAssignments) {
        AtomicDouble finished = new AtomicDouble();
        AtomicDouble planned = new AtomicDouble();
        AtomicDouble unplanned = new AtomicDouble();

        user.getOccurrences().stream()
                .filter(o -> o.getStatus() == UserOccurrence.OccurrenceStatus.FINISHED
                        && o.getAssignment().getCourse().equals(c))
                .forEach(o -> {
                    long length = o.getActualLength().longValue();
                    if (length == 0.0) {
                        length = TimeUnit.MILLISECONDS.toHours(o.getEnd_time() - o.getStart_time());
                    }
                    finished.addAndGet(length);
                });

        user.getOccurrences().stream()
                .filter(o -> o.getStatus() == UserOccurrence.OccurrenceStatus.UNFINISHED
                        && o.getAssignment().getCourse().equals(c))
                .forEach(o -> planned.addAndGet(TimeUnit.MILLISECONDS.toHours(o.getEnd_time() - o.getStart_time())));

        c.getAssignments().stream()
                .filter(a -> !alreadyPlannedAssignments.contains(a))
                .forEach(a -> unplanned.addAndGet(a.getLength()));

        return new double[] {finished.get(), planned.get(), unplanned.get()};
    }

    private ListResponse<AssignmentResponse> createListResponse(Collection<Assignment> assignments) {
        return createListResponse(assignments, AssignmentResponse::from);
    }
}
