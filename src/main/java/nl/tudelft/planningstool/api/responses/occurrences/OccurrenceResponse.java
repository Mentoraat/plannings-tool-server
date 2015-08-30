package nl.tudelft.planningstool.api.responses.occurrences;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public abstract class OccurrenceResponse {

    private static final DateFormat DAY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat HOUR_FORMATTER = new SimpleDateFormat("HH:mm:ss");

    private String start;

    private String end;

    private String editable;

    private String title;

    public void process(Occurrence occurrence, String editable, String title) {
        this.setStart(parseTime(occurrence.getStart_time()));
        this.setEnd(parseTime(occurrence.getEnd_time()));
        this.setEditable(editable);
        this.setTitle(title);
    }

    private String parseTime(long time) {
        Date date = new Date(time);
        return DAY_FORMATTER.format(date) + "T" + HOUR_FORMATTER.format(date);
    }

}
