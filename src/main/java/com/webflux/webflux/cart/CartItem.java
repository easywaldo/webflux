package com.webflux.webflux.cart;

import lombok.Getter;

@Getter
public class CartItem {
    private Item item;
    private int quantity;

    public CartItem(Item item) {
        this.item = item;
    }

    public CartItem(Item item,
                     int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void increment() {
        this.quantity += 1;
    }
}
