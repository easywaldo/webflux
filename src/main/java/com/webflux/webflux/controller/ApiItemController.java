package com.webflux.webflux.controller;

import com.webflux.webflux.annotation.MyTimer;
import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import com.webflux.webflux.dto.RequestSampleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiItemController {
    private final ItemRepository repository;
    private final Validator validator;

    @Autowired
    public ApiItemController(ItemRepository repository,
                             Validator validator) {
        this.repository = repository;
        this.validator = validator;
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

    @PutMapping("/api/items/{id}")
    Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item, @PathVariable String id) {
            return item.map(content -> new Item(id, content.getItemName(), content.getItemPrice()))
            .flatMap(this.repository::save)
            .map(ResponseEntity::ok);
    }

    @MyTimer
    @DeleteMapping("/longTask")
    public void longTask() throws InterruptedException {

        Thread.sleep(5000);

        /*StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Thread.sleep(5000);

        stopWatch.stop();*/
        //System.out.println("totalTime: " + stopWatch.getTotalTimeMillis());
    }

    @PostMapping(value = "/api/requestTest")
    public void requestTest(@Valid @RequestBody RequestSampleDto sampleDto) {
        List<ConstraintViolation<RequestSampleDto>> violations = this.validator.validate(sampleDto)
            .stream().collect(Collectors.toList());
        if(violations.stream().count() > 0) {
            System.out.println("parameter error");
        }
    }
}
