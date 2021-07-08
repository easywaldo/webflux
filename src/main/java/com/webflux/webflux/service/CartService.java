package com.webflux.webflux.service;

import com.webflux.webflux.cart.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CartService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public CartService(ItemRepository itemRepository,
                       CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public Flux<Item> findAllItems() {
        return this.itemRepository.findAll();
    }

    public Mono<Cart> findCartItemById(String id) {
        return this.cartRepository.findById(id);
    }

    public Mono<Cart> addToCart(String cartId, String id) {
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
            .flatMap(cart -> this.cartRepository.save(cart));
    }
}
