package nl.tudelft.planningstool.api.responses;

import lombok.Data;
import nl.tudelft.planningstool.database.entities.User;

import java.util.UUID;

@Data
public class UserResponse {

    private int id;

    private String name;

    private String accessToken;

    private String uuid;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setAccessToken(user.getAccessToken());
        response.setUuid(user.getUuid());

        return response;
    }
}
