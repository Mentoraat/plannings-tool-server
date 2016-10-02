package nl.tudelft.planningstool.api.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResetPasswordForm implements Serializable {
    private String email;
    private String username;
}
