package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Cart;
import com.webflux.webflux.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/cart")
    public Mono<Rendering> cartList() {
        return Mono.just(Rendering.view("cart.html")
            .modelAttribute("items",
                this.cartService.findAllItems())
            .modelAttribute("cart",
                this.cartService.findCartItemById("My Cart").defaultIfEmpty(new Cart("My Cart")))
            .build());
    }

    @PostMapping(value="/cart/add/{id}")
    public Mono<String> addToCart(@PathVariable String id) {
        return this.cartService.addToCart("My Cart", id)
            .thenReturn("redirect:/cart");
    }

    @DeleteMapping(value="/cart/delete/{id}")
    public Mono<String> deleteToCart(@PathVariable String id) {
        return this.cartService.deleteToCart("My Cart", id)
            .thenReturn("redirect:/cart");
    }

}
