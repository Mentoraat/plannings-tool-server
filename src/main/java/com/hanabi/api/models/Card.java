package com.hanabi.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by tim on 9-7-15.
 */
public class Card {

    @Setter
    private Color color;

    @Getter
    @Setter
    private Object value;

    @JsonProperty
    public String getColor() {
        return this.color.getRepresentation();
    }

}
