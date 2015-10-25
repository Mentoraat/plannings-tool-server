package nl.tudelft.planningstool.api.responses.occurrences;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;

import java.util.UUID;

/**
 * A {@link OccurrenceResponse} which is for a single user-assignment.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOccurrenceResponse extends OccurrenceResponse {

    private AssignmentResponse assignment;

    private OccurrenceUserResponse user;

    private UserOccurrence.OccurrenceStatus status;

    public static UserOccurrenceResponse from(UserOccurrence occurrence) {
        UserOccurrenceResponse response = new UserOccurrenceResponse();

        response.setAssignment(AssignmentResponse.from(occurrence.getAssignment()));
        response.setUser(OccurrenceUserResponse.from(occurrence.getUser()));
        response.setStatus(occurrence.getStatus());

        response.process(occurrence, true, occurrence.getAssignment().getName());

        return response;
    }

    @Data
    public static class OccurrenceUserResponse {

        private int id;

        private String name;

        private String accessToken;

        private UUID uuid;

        public static OccurrenceUserResponse from(User user) {
            OccurrenceUserResponse response = new OccurrenceUserResponse();

            response.setId(user.getId());
            response.setName(user.getName());
            response.setAccessToken(user.getAccessToken());
            response.setUuid(user.getUuid());

            return response;
        }
    }
}
