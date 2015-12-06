package nl.tudelft.planningstool.api.v1;

import nl.tudelft.planningstool.api.security.Secured;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.Assignment;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.CourseOccurrence;
import nl.tudelft.planningstool.database.entities.courses.Course;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.function.Consumer;

@Path("v1/courses")
@Secured
public class CourseOccurrenceAPI extends ResponseAPI {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    static {
        TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("Amsterdam"));
    }

    @POST
    @Path("lectures")
    @Consumes("multipart/form-data")
    public String uploadLectures(MultipartFormDataInput input) {
        return parseFileInput(input, (sc) -> {
            // Start of the week
            sc.nextLine();
            // Headers
            sc.nextLine();

            String s;
            // Weeks are split by an empty line
            while (sc.hasNextLine() && !(s = sc.nextLine()).equals("")) {
                createOccurrenceFromLine(s);
            }
        });
    }

    @POST
    @Path("assignments")
    @Consumes("multipart/form-data")
    public String uploadAssignments(MultipartFormDataInput input) {
        return parseFileInput(input, (sc) -> {
            String[] parts = sc.nextLine().split(";");

            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].replaceAll("\"", "");
            }

            Assignment assignment = new Assignment();
            assignment.setLength(Integer.valueOf(parts[2]));
            assignment.setName(parts[3]);
            assignment.setDescription(parts[4]);
            assignment.setDeadlineAsWeek(Integer.valueOf(parts[5]));

            Course course = this.courseDAO.getFromEdition(parts[0], Integer.valueOf(parts[1]));
            course.addAssignment(assignment);

            this.courseDAO.merge(course);
        });
    }

    private String parseFileInput(MultipartFormDataInput input,
                                  ScannerConsumer parser) {
        List<InputPart> inputParts = input.getFormDataMap().get("file");

        for (InputPart part : inputParts) {
            try {
                String fileContent = part.getBodyAsString();
                Scanner sc = new Scanner(fileContent);

                while (sc.hasNextLine()) {
                    parser.scan(sc);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("No file attached");
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid file format");
            }
        }

        return "Upload successful";
    }

    private void createOccurrenceFromLine(String s) throws ParseException {
        String[] parts = s.split(";");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\"", "");
        }

        String courseId = parts[0];
        String day = parts[4];
        String startTimeString = parts[5];
        String[] duration = parts[9].split(":");

        long startTime;

        synchronized (TIME_FORMATTER) {
            startTime = TIME_FORMATTER.parse(day + "-" + startTimeString).getTime();
        }
        double durationLong = Double.valueOf(duration[0]) + (Double.valueOf(duration[1]) / 60.0);

        CourseOccurrence o = new CourseOccurrence();
        o.plan(startTime, durationLong);

        // TODO: Handle not-found course
        // TODO: Specify how to find year
        Course course = this.courseDAO.getFromEdition(courseId, Integer.valueOf(day.split("-")[0]));
        course.addOccurrence(o);
        this.courseDAO.merge(course);
    }

    private interface ScannerConsumer {

        void scan(Scanner sc) throws ParseException;
    }
}
