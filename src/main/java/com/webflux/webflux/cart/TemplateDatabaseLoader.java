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
            mongo.save(Item.builder().itemName("watermelon").itemPrice(23000).build());
            mongo.save(Item.builder().itemName("ice maker").itemPrice(70000).build());
            mongo.save(Item.builder().itemName("green tea\"").itemPrice(4800).build());
            mongo.save(Item.builder().itemName("orange juice").itemPrice(10000).build());
        };
    }
}
