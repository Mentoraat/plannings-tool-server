package nl.tudelft.planningstool.api.responses.occurrences;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.planningstool.api.responses.AssignmentResponse;
import nl.tudelft.planningstool.api.responses.UserResponse;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;

/**
 * A {@link OccurrenceResponse} which is for a single user-assignment.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOccurrenceResponse extends OccurrenceResponse {

    private AssignmentResponse assignment;

    private UserResponse user;

    private UserOccurrence.OccurrenceStatus status;

    private Double actualLength;

    private String notes;

    public static UserOccurrenceResponse from(UserOccurrence occurrence) {
        UserOccurrenceResponse response = new UserOccurrenceResponse();

        response.setAssignment(AssignmentResponse.from(occurrence.getAssignment()));
        response.setUser(UserResponse.from(occurrence.getUser()));
        response.setStatus(occurrence.getStatus());
        response.setActualLength(occurrence.getActualLength());
        response.setNotes(occurrence.getNotes());

        response.process(occurrence, true, occurrence.getAssignment().getName());

        return response;
    }
}
