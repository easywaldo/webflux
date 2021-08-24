package com.webflux.webflux;

import com.webflux.webflux.cart.*;
import com.webflux.webflux.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class InventoryServiceTest {

    CartService cartService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CartRepository cartRepository;
    public Item sampleItem;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        cartRepository = mock(CartRepository.class);

        //arrange
        sampleItem = new Item("001", "TV", 20000);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("my-cart", Collections.singletonList(sampleCartItem));

        //when
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        cartService = new CartService(itemRepository, cartRepository);
    }

    @Test
    void add_item_to_empty_cart_should_produce_one_cart_item() {
        cartService.addToCart("my-cart", "001")
            .as(StepVerifier::create)
            .expectNextMatches(cart -> {
                assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                    .containsExactlyInAnyOrder(1);
                assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                    .containsExactly(sampleItem);
                return true;
            })
            .verifyComplete();
    }




}
