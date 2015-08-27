package nl.tudelft.planningstool.api.responses.occurrences;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;

import java.util.UUID;

@Data
public abstract class OccurrenceResponse {

    private long start_time;

    private long end_time;

    public void process(Occurrence occurrence) {
        this.setStart_time(occurrence.getStart_time());
        this.setEnd_time(occurrence.getEnd_time());
    }

}
