package com.webflux.webflux.cart;

import lombok.Getter;

@Getter
public class CartItem {
    private Item item;
    private int quantity;

    private CartItem() {}

    public CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }

    public void increment() {
        this.quantity += 1;
    }

    public void decrement() {
        if (this.quantity == 0) return;
        this.quantity -= 1;
    }
}
