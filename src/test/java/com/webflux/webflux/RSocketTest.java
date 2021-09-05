package com.webflux.webflux;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class RSocketTest {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void verify_remote_operation_through_rsocket_request_response() throws InterruptedException {
        // arrange
        this.itemRepository.deleteAll()
            .as(StepVerifier::create)
            .verifyComplete();

        // act
        this.webTestClient.post().uri("/items/request-response")
            .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Item.class)
            .value(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getItemName()).isEqualTo("nothing important");
                assertThat(item.getItemPrice()).isEqualTo(19.99);
            });

        Thread.sleep(500);

        // assert
        this.itemRepository.findAll()
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getItemName()).isEqualTo("nothing important");
                assertThat(item.getItemPrice()).isEqualTo(19.99);
                return true;
            })
            .verifyComplete();

    }
}
