package com.hanabi.interfaces;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tim on 8-7-15.
 */
public abstract class Encapsulating<E extends Encapsulatable> {

    @Getter
    @Setter
    private E encapsulatable;

}
