package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Cart;
import com.webflux.webflux.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {
    private final CartService cartService;

    public HomeController(CartService cartService) {
        this.cartService = cartService;
    }

    /*@GetMapping(value = "/home")
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("cart.html")
            .modelAttribute("items", this.cartService.findAllItems())
            .modelAttribute("cart", this.cartService.findCartItemById("My Cart").defaultIfEmpty(new Cart("My Cart")))
            .build());
    }*/
}
