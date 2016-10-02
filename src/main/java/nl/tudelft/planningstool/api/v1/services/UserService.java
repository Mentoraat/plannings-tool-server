package nl.tudelft.planningstool.api.v1.services;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.controllers.UserDAO;
import nl.tudelft.planningstool.database.entities.User;
import nl.tudelft.planningstool.database.entities.assignments.occurrences.UserOccurrence;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UserService {

    @Inject
    private UserDAO userDAO;

    public void removeWeekOldEvents(User user) {
        Set<UserOccurrence> occurrences = user.getOccurrences();
        long currentTime = System.currentTimeMillis();user.getOccurrences().stream()
                .filter(o -> o.getStatus() != UserOccurrence.OccurrenceStatus.FINISHED &&
                        !o.getAssignment().getCourse().getCourseName().equals("Personal events") &&
                        o.getEnd_time() < currentTime - TimeUnit.DAYS.toMillis(7))
                .forEach(occurrences::remove);

        this.userDAO.merge(user);
    }
}
