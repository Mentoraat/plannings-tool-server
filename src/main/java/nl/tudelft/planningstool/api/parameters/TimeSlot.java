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

    @SneakyThrows
    private long getTimeLong(String queryParam, String name) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameter '" + name + "' not supplied");
        }

        try {
            if (!queryParam.matches("\\d{4}-[0-1]\\d-[0-3]\\d")) {
                throw new Exception();
            }

            return DAY_FORMATTER.parse(queryParam).getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal " + name + " time format");
        }
    }

}
