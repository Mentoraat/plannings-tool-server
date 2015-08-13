package nl.tudelft.planningstool.database.entities;

import javax.ws.rs.ForbiddenException;

/**
 * Check if the user has the correct permission.
 */
public interface AdminVerifiable {

    void checkAdmin() throws ForbiddenException;
}
