
package nl.tudelft.planningstool.api.v1;

import com.google.inject.Inject;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.api.responses.occurrences.UserOccurrenceResponse;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.occurrences.OccurrenceResponse;
import nl.tudelft.planningstool.database.DatabaseTestModule;
import nl.tudelft.planningstool.database.bootstrapper.BootstrapRule;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class UserOccurrenceAPITest {

    private static final String USER_UUID = "025e6e3e-7eea-44c3-b75a-5e959fad94e3";

    private static final String COURSE_UUID = "aba62cd5-caa6-4e42-a5d6-4909f03038bf";

    private static final SimpleDateFormat DAY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @Rule
    @Inject
    public BootstrapRule bootstrapRule;

    @Rule
    public ExpectedException expected = ExpectedException.none();

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

        assertThat(response.size()).isEqualTo(7);
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

        long now = System.currentTimeMillis();

        slot.setStart(DAY_FORMATTER.format(new Date(now + TimeUnit.DAYS.toMillis(-2))));
        slot.setEnd(DAY_FORMATTER.format(new Date(now + TimeUnit.DAYS.toMillis(3))));

        return slot;
    }
}
