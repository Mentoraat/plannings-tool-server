package com.hanabi.api.models;

import com.hanabi.interfaces.Encapsulatable;
import com.hanabi.backend.game.GameInstance;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by tim on 8-7-15.
 */
@Data
public class Game implements Encapsulatable {

    private int id;

    private List<Player> players;
}
