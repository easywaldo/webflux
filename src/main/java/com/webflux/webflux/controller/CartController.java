package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Cart;
import com.webflux.webflux.cart.CartItem;
import com.webflux.webflux.cart.CartRepository;
import com.webflux.webflux.cart.ItemRepository;
import com.webflux.webflux.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
            .modelAttribute("items", this.cartService.findAllItems())
            .modelAttribute("cart", this.cartService.findCartItemById("My Cart").defaultIfEmpty(new Cart("My Cart")))
            .build());
    }

    @PostMapping("/add/{id}")
    public Mono<String> addToCart(@PathVariable String id) {
        return this.cartService.addToCart("My Cart", id)
            .thenReturn("redirect:/");
    }

}
