package com.hanabi.api.models;

import com.hanabi.interfaces.Encapsulatable;
import com.hanabi.backend.game.GameInstance;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tim on 8-7-15.
 */
@Data
@NoArgsConstructor
public class Game implements Encapsulatable {

    private int id;
}
