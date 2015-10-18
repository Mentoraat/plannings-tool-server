package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.api.responses.occurrences.CourseOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import util.TestBase;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class UserOccurrenceAPITest extends TestBase {

    private static final String USER_UUID = "aba62cd5-caa6-4e42-a5d6-4909f03038bf";

    private static final String COURSE_UUID = "aba62cd5-caa6-4e42-a5d6-4909f03038bf";

    @Inject
    private UserOccurrenceAPI api;

    @Test
    @TestBootstrap("default.json")
    public void should_provide_occurrences() throws Exception {
        Collection<? super OccurrenceResponse> response = this.api.get(USER_UUID, makeTimeSlot());

        ArrayList<? super OccurrenceResponse> list = new ArrayList<>(response);
        Collections.sort(list, (one, other) -> {
            if (one.getClass().equals(other.getClass())) {
                return 0;
            }

            return one instanceof UserOccurrenceResponse ? -1 : 1;
        });

        assertThat(response.size()).isEqualTo(3);
        assertThat(list.get(0)).isInstanceOf(UserOccurrenceResponse.class);
        assertThat(list.get(2)).isInstanceOf(CourseOccurrenceResponse.class);
    }

    @Test
    @TestBootstrap("default.json")
    public void should_provide_occurrences_for_course() {
        ListResponse<UserOccurrenceResponse> response = this.api.getWithCourse(USER_UUID, COURSE_UUID);

        assertThat(response.getTotal_items()).isEqualTo(1);
        assertThat(response.getItems().get(0).getAssignment().getId()).isEqualTo(1);
    }

    private TimeSlot makeTimeSlot() {
        TimeSlot slot = new TimeSlot();

        slot.setStart("2015-08-23");
        slot.setEnd("2015-08-30");

        return slot;
    }
}
