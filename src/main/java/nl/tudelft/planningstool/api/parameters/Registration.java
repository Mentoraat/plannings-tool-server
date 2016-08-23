package nl.tudelft.planningstool.api.parameters;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Provide a serializable class, used to consume authentication details.
 */
@Data
@ToString(callSuper = true)
public class Registration extends Credentials {
    private String email;
    private int studentnumber;
}
