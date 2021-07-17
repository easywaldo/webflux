package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class ApiItemController {
    private final ItemRepository repository;

    @Autowired
    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/items")
    public Flux<Item> findAllItems() {
        return this.repository.findAll();
    }

    @GetMapping("/api/items/{id}")
    public Mono<Item> findOne(@PathVariable String id) {
        return this.repository.findById(id);
    }

    @PostMapping("/api/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(s -> this.repository.save(s))
            .map(savedItem -> ResponseEntity.created(URI.create(String.format("/api/items/%s", savedItem.getId())))
                .body(savedItem));
    }
}
