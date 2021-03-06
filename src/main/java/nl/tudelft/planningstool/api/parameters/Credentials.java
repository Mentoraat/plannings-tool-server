package nl.tudelft.planningstool.api.parameters;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Provide a serializable class, used to consume authentication details.
 */
@Data
@ToString(exclude = "password")
public class Credentials implements Serializable {
    private String username;
    private String password;
}
