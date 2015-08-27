package nl.tudelft.planningstool.api;

import com.google.inject.Inject;
import util.TestBase;
import nl.tudelft.planningstool.api.responses.ListResponse;
import nl.tudelft.planningstool.api.responses.OccurrenceResponse;
import nl.tudelft.planningstool.api.v1.UserOccurrenceAPI;
import nl.tudelft.planningstool.database.bootstrapper.TestBootstrap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserOccurrenceAPITest extends TestBase {

    private static final String USER_UUID = "aba62cd5-caa6-4e42-a5d6-4909f03038bf";

    private static final String COURSE_UUID = "aba62cd5-caa6-4e42-a5d6-4909f03038bf";

    @Inject
    private UserOccurrenceAPI api;

    @Test
    @TestBootstrap("default.json")
    public void should_provide_occurrences() {
        ListResponse<OccurrenceResponse> response = this.api.getWithCourse(USER_UUID, COURSE_UUID);

        assertThat(response.getTotal_items()).isEqualTo(1);
        assertThat(response.getItems().get(0).getAssignment().getId()).isEqualTo(1);
    }
}
