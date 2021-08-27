package com.webflux.webflux.service;

import com.webflux.webflux.cart.Item;
import com.webflux.webflux.cart.ItemRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Controller
public class RSocketService {
    private final EmitterProcessor<Item> itemProcessor;
    private final ItemRepository itemRepository;
    private final FluxSink<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemProcessor = EmitterProcessor.create();
        this.itemSink = this.itemProcessor.sink();
    }

    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemViaRSocketRequestResponse(Item item) {
        return this.itemRepository.save(item)
            .doOnNext(savedItem -> this.itemSink.next(savedItem));
    }

    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaRSocketRequestStream() {
        return this.itemRepository.findAll()
            .doOnNext(this.itemSink::next);
    }

    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
        return this.itemRepository.save(item)
            .doOnNext(savedItem -> this.itemSink.next(savedItem))
            .then();
    }

    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems() {
        return this.itemProcessor;
    }
}
