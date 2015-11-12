package nl.tudelft.planningstool.api.responses;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by gijs on 21-11-15.
 */
@Data
public class TokenResponse implements Serializable {
    private String token;
    private long endOfValidity;
}
