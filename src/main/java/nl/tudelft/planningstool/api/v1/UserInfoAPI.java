package nl.tudelft.planningstool.api.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.planningstool.api.responses.CourseEditionResponse;
import nl.tudelft.planningstool.api.responses.UserResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import nl.tudelft.planningstool.database.entities.courses.CourseRelation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("v1/users/USER-{userId: .+}")
public class UserInfoAPI extends ResponseAPI {

    private static final List<String> COLORS = Lists.newArrayList("#ff6447", "#5441b0", "#708090");

    @GET
    public UserCoursesResponse get(@PathParam("userId") String userId) {
        User user = this.userDAO.getFromUUID(userId);

        Map<CourseEditionResponse, String> map = new TreeMap<>();

        user.getCourses().stream()
                .map(CourseRelation::getCourse)
                .map(Course::getEdition)
                .map(CourseEditionResponse::from)
                .forEach(e -> map.put(e, null));

        int i = 0;

        for (CourseEditionResponse r : map.keySet()) {
            if (i == COLORS.size()) {
                break;
            }

            map.put(r, COLORS.get(i));
            i++;
        }

        UserCoursesResponse r = new UserCoursesResponse();
        r.setUser(UserResponse.from(user));
        r.setColors(map);

        cleanUpWeekOldOccurrences(user);

        return r;
    }

    private void cleanUpWeekOldOccurrences(User user) {
        long weekago = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);
        user.getOccurrences().removeIf(o -> o.getStart_time() <= weekago && o.getStatus() == UserOccurrence.OccurrenceStatus.UNFINISHED);
        this.userDAO.merge(user);
    }

    @GET
    @Path("/courses")
    public List<CourseEditionResponse> getCourses(@PathParam("userId") String userId) {
        return this.userDAO.getFromUUID(userId)
                .getCourses().stream()
                .map(CourseRelation::getCourse)
                .map(Course::getEdition)
                .map(CourseEditionResponse::from)
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class UserCoursesResponse {

        private UserResponse user;

        private Map<CourseEditionResponse, String> colors;
    }
}
