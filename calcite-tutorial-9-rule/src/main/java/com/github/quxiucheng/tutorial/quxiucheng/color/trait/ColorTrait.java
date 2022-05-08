package com.github.quxiucheng.tutorial.quxiucheng.color.trait;

import org.apache.calcite.plan.RelTrait;

import java.util.Objects;

/**
 * 必须是单例
 * @author quxiucheng
 * @date 2022-05-08 09:17:00
 */
public abstract class ColorTrait implements RelTrait {
    protected String color;

    public ColorTrait(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) {
            return true;
        }
        if(otherObject == null) {
            return false;
        }

        if(getClass() != otherObject.getClass()) {
            return false;
        }

        ColorTrait other = (ColorTrait) otherObject;

        return Objects.equals(this.color, other.getColor());
    }
    @Override
    public int hashCode() {

        return Objects.hash(color);
    }

}

