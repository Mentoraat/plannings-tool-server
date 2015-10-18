package nl.tudelft.planningstool.api.parameters;

import lombok.Data;
import lombok.SneakyThrows;

import javax.ws.rs.QueryParam;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;
import java.util.TimeZone;
import java.util.spi.TimeZoneNameProvider;

/**
 * TimeSlot can be used in {@link QueryParam} to parse a time-format. Whenever you include TimeSlot, its value
 * must be provided.
 *
 * The default TimeZone is Amsterdam.
 * Valid time-formats are: yyyy-MM-dd
 */
@Data
public class TimeSlot {

    private static final SimpleDateFormat DAY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    static {
        DAY_FORMATTER.setTimeZone(TimeZone.getTimeZone("Amsterdam"));
    }

    private long start;

    private long end;

    @QueryParam("start")
    public void setStart(String start) {
        this.start = getTimeLong(start, "start");
    }

    @QueryParam("end")
    public void setEnd(String end) {
        this.end = getTimeLong(end, "end");
    }

    private static long getTimeLong(String value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("Query parameter '" + name + "' not supplied");
        }

        try {
            if (!value.matches("\\d{4}-[0-1]\\d-[0-3]\\d")) {
                throw new Exception();
            }

            synchronized (DAY_FORMATTER) {
                return DAY_FORMATTER.parse(value).getTime();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal " + name + " time format");
        }
    }

}
