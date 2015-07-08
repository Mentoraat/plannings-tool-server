package com.hanabi.api.models;

import lombok.Getter;

/**
 * Created by tim on 9-7-15.
 */
public enum Color {

    BLUE("Blue"),
    RED("Red"),
    GREEN("Green"),
    YELLOW("Yellow"),
    WHITE("White"),
    UNKNOWN("?");

    @Getter
    private final String representation;

    private Color(String representation) {
        this.representation = representation;
    }
}
