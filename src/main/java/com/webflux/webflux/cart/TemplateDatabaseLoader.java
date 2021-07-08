package com.webflux.webflux.cart;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class TemplateDatabaseLoader {
    @Bean(value = "template_loader")
    public CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(new Item("watermelon", 23000));
            mongo.save(new Item("ice maker", 70000));
        };
    }
}
