package nl.tudelft.planningstool.api.parameters;

import lombok.Data;

import java.io.Serializable;

/**
 * Provide a serializable class, used to consume authentication details.
 */
@Data
public class Credentials implements Serializable {
    private String username;
    private String password;
}
