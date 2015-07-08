package com.hanabi.backend.game;

import com.hanabi.api.models.Color;
import com.hanabi.api.models.Player;
import com.hanabi.interfaces.Encapsulating;
import lombok.Data;

/**
 * Created by tim on 8-7-15.
 */
@Data
public class PlayerInstance extends Encapsulating<Player> {

    private int id;

    public PlayerInstance() {
        this.setEncapsulatable(new Player());
    }

    @Override
    public Player transform() {
        return this.getEncapsulatable();
    }

    public void addCard(Color color, int value) {
        this.getEncapsulatable().addCard(color, value);
    }
}
