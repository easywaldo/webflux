package com.webflux.webflux.repository;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
public class MongoDbSliceTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void itemRepository_saveTest() {
        // arrange
        Item sampleItem = Item.builder().itemName("name").itemPrice(20000).build();

        // act
        itemRepository.save(sampleItem)
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getItemName()).isEqualTo("name");
                assertThat(item.getItemPrice()).isEqualTo(20000);
                return true;
            }).verifyComplete();
    }
}
