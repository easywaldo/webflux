package com.webflux.webflux;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    public void verify_remote_operation_through_rsocket_request_stream() {
        // arrange
        this.itemRepository.deleteAll().block();

        List<Item> items = IntStream.rangeClosed(1, 3)
            .mapToObj(m -> new Item("id-" + m, "name-" + m, m))
            .collect(Collectors.toList());

        this.itemRepository.saveAll(items).blockLast();

        // act
        this.webTestClient.get().uri("/items/request-stream")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk()
            .returnResult(Item.class)
            .getResponseBody()
            .as(StepVerifier::create)
            .expectNextMatches(itemPredicate("1"))
            .expectNextMatches(itemPredicate("2"))
            .expectNextMatches(itemPredicate("3"))
            .verifyComplete();
    }

    private Predicate<Item> itemPredicate(String num) {
        return item -> {
            assertThat(item.getItemName()).startsWith("name");
            assertThat(item.getItemPrice()).isPositive();
            assertThat(item.getId()).startsWith("id");
            assertThat(item.getItemPrice()).isEqualTo(Double.parseDouble(num));
            return true;
        };
    }
}
