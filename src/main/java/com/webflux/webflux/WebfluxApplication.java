package com.webflux.webflux;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WebfluxApplication {
    public static void main(String[] args) {
        /*BlockHound.builder().allowBlockingCallsInside(
            TemplateEngine.class.getCanonicalName(), "process")
            .install();*/
        SpringApplication.run(WebfluxApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
        ItemRepository itemRepository) {

        return (args) -> {
            itemRepository.deleteAll();
            itemRepository.save(Item.builder()
                .itemName("iphone")
                .id("ipsx-13")
                .itemPrice(130000L)
                .build());
        };
    }

}
