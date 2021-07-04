package com.webflux.webflux;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public class Dish {
    private String name;
    private boolean isDelivered;

    @ConstructorProperties({"name", "isDelivered"})
    public Dish(String name) {
        this.name = name;
        this.isDelivered = false;
    }

    public static Dish deliver(Dish dish) {
        Dish deliveredDish = new Dish(dish.name);
        deliveredDish.isDelivered = true;
        return dish;
    }
}
