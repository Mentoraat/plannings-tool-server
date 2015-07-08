package com.hanabi.api.models;

import com.google.common.collect.Lists;
import com.hanabi.interfaces.Encapsulatable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by tim on 8-7-15.
 */
@Data
public class Player implements Encapsulatable {

    private List<Card> cards;

    public Player() {
        this.cards = Lists.newArrayList();
    }

    public void addCard(Color color, int value) {
        Card card = new Card();
        card.setColor(color);
        card.setValue(value);

        cards.add(card);
    }
}
