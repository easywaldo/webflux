package com.webflux.webflux;

import com.webflux.webflux.cart.BlockingItemRepository;
import com.webflux.webflux.cart.RepositoryDatabaseLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebfluxApplication {
    private static BlockingItemRepository blockingItemRepository;
    @Autowired
    private RepositoryDatabaseLoader loader;

    public static void main(String[] args) {
        RepositoryDatabaseLoader loader = new RepositoryDatabaseLoader();
        loader.initialize(blockingItemRepository);
        SpringApplication.run(WebfluxApplication.class, args);
    }

}
