package nl.tudelft.planningstool.api.responses.occurrences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import nl.tudelft.planningstool.api.parameters.TimeSlot;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.Occurrence;

import java.text.DateFormat;
import java.text.ParseException;
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

    @JsonIgnore
    public long getStartTime() {
        return parseTime(start, "start");
    }

    @JsonIgnore
    public long getEndTime() {
        return parseTime(end, "end");
    }

    private static String parseTime(long time) {
        Date date = new Date(time);
        return DAY_FORMATTER.format(date) + "T" + HOUR_FORMATTER.format(date);
    }

    private static long parseTime(String time, String name) {
        if (!time.contains(",")) {
            throw new IllegalArgumentException("Illegal " + name + " time format");
        }

        String[] split = time.split(",");
        try {
            return DAY_FORMATTER.parse(split[0]).getTime() + HOUR_FORMATTER.parse(split[1]).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal " + name + " time format");
        }
    }

}
