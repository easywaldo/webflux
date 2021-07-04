package com.webflux.webflux.controller;

import com.webflux.webflux.Dish;
import com.webflux.webflux.KitchenService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ServerController {
    private final KitchenService kitchenService;

    public ServerController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @GetMapping(value = "/served-dishes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Dish> deliverDishes() {
      return this.kitchenService.getDishes();
    }


}
