package com.webflux.webflux;

import reactor.core.publisher.Flux;

public class KitchenService {
    Flux<Dish> getDishes() {
      return Flux.just(
          new Dish("Americano"), new Dish("Cappuchino"), new Dish("Fruit juice")
      );
    }
}
