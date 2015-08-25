package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Occurrence;

import java.util.UUID;

@Data
public class OccurrenceResponse {

    private AssignmentResponse assignment;

    private OccurrenceUserResponse user;

    private long startingAt;

    private double length;

    public static OccurrenceResponse from(Occurrence occurrence) {
        OccurrenceResponse response = new OccurrenceResponse();

        response.setAssignment(AssignmentResponse.from(occurrence.getAssignment()));
        response.setUser(OccurrenceUserResponse.from(occurrence.getUser()));
        response.setStartingAt(occurrence.getStartingAt());
        response.setLength(occurrence.getLength());

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
