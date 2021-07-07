package com.webflux.webflux.cart;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RepositoryDatabaseLoader {
    @Bean
    CommandLineRunner initialze(BlockingItemRepository repository) {
        return args ->{
            repository.save(new Item("green tea", 4800));
            repository.save(new Item("orange juice", 10000));
        };
    }
}
