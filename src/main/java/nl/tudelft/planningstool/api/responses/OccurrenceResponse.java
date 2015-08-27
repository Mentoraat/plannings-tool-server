package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;

import java.util.UUID;

@Data
public class OccurrenceResponse {

    private AssignmentResponse assignment;

    private OccurrenceUserResponse user;

    private long start_time;

    private long end_time;

    public static OccurrenceResponse from(Occurrence occurrence) {
        OccurrenceResponse response = new OccurrenceResponse();

        response.setAssignment(AssignmentResponse.from(occurrence.getAssignment()));
        response.setUser(OccurrenceUserResponse.from(occurrence.getUser()));
        response.setStart_time(occurrence.getStart_time());
        response.setEnd_time(occurrence.getEnd_time());

        return response;
    }

    @Data
    private static class OccurrenceUserResponse {

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
