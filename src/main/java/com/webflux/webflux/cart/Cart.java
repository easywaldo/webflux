package com.webflux.webflux.cart;

import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cart {
    @Id
    private String id;
    private List<CartItem> cartItems;

    private Cart() {}

    public Cart(String id) {
        this.id = id;
        this.cartItems = new ArrayList<>();
    }

    public Cart(String id,
                List<CartItem> cartItems) {
        this.id = id;
        this.cartItems = cartItems;
    }
}
