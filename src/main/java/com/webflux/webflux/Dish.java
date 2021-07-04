package com.webflux.webflux;

import java.beans.ConstructorProperties;

public class Dish {
    private String name;
    private boolean isDelivered;

    @ConstructorProperties({"name", "isDelivered"})
    public Dish(String name) {
        this.name = name;
        this.isDelivered = false;
    }

    public static Dish deliver(Dish dish) {
        dish.isDelivered = true;
        return dish;
    }
}
