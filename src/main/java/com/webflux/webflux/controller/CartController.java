package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Cart;
import com.webflux.webflux.cart.CartItem;
import com.webflux.webflux.cart.CartRepository;
import com.webflux.webflux.cart.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class CartController {
    private ItemRepository itemRepository;
    private CartRepository cartRepository;

    @Autowired
    public CartController(ItemRepository itemRepository,
                          CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping()
    public Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
            .modelAttribute("items", this.itemRepository.findAll())
            .modelAttribute("cart", this.cartRepository.findById("My Cart").defaultIfEmpty(new Cart("My Cart")))
            .build());
    }

    @PostMapping
    public Mono<String> addToCart(@PathVariable String id) {
        return this.cartRepository.findById("My Cart")
            .defaultIfEmpty(new Cart("My cart"))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(id))
                .findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart);
                })
                .orElseGet(() -> {
                    return this.itemRepository.findById(id)
                        .map(item -> new CartItem(item))
                        .map(cartItem -> {
                            cart.getCartItems().add(cartItem);
                            return cart;
                        });
                }))
            .flatMap(cart -> this.cartRepository.save(cart))
            .thenReturn("redirect:/");
    }

}
