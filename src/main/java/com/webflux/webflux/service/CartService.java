package com.webflux.webflux.service;

import com.webflux.webflux.cart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CartService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartService(ItemRepository itemRepository,
                       CartRepository cartRepository){
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
        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart("My Cart"))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(id))
                .findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart);
                })
                .orElseGet(() ->
                    this.itemRepository.findById(id)
                        .map(CartItem::new)
                        .doOnNext(cartItem ->
                            cart.getCartItems().add(cartItem))
                        .map(cartItem -> cart)))
            .flatMap(this.cartRepository::save);
    }

    public Mono<Object> deleteToCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart("My Cart"))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findFirst()
                .map(cartItem -> {
                    if (cartItem.getQuantity() == 0) {
                        cart.getCartItems().remove(cartItem);
                    }
                    else {
                        cartItem.decrement();
                    }
                    return Mono.just(cart);
                })
                .orElseGet(() ->
                    this.cartRepository.findById(cartId)
                ))
            .flatMap(this.cartRepository::save);
    }

    public Flux<Item> searchItems(String name, double itemPrice, boolean useAnd) {
        Item item = Item.builder().itemName(name).itemPrice(itemPrice).build();
        ExampleMatcher matcher = useAnd ?
            ExampleMatcher.matchingAll()
            : ExampleMatcher.matchingAny()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price");
        Example<Item> probe = Example.of(item, matcher);
        return itemRepository.findAll(probe);
    }
}
