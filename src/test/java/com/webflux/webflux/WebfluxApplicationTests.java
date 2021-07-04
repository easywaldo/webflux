package com.webflux.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class WebfluxApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void given_flux_dish() {
        KitchenService kitchenService = new KitchenService();
        Flux<Dish> dishFlux =  kitchenService.getDishes()
            .doOnNext(dish -> System.out.println("Thank u for dish !"))
            .doOnError(error -> System.out.println("Sorry about " + error.getMessage()))
            .doOnComplete(() -> System.out.println("Thanks for all your hard work!"))
            .map(dish -> Dish.deliver(dish));

        dishFlux.subscribe();
    }

}
