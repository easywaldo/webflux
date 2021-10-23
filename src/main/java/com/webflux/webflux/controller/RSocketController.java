package com.webflux.webflux.controller;

import com.webflux.webflux.cart.Item;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.*;

@RestController
public class RSocketController {
    private final Mono<RSocketRequester> requester;

    public RSocketController(RSocketRequester.Builder builder) {
        this.requester = builder
            .dataMimeType(APPLICATION_JSON)
            .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
            .connectTcp("localhost", 7000)
            .retry(5)
            .cache();
    }

    @PostMapping("/items/request-response")
    public Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        return this.requester
            .flatMap(rSocketRequester -> rSocketRequester
                .route("newItems.request-response").data(item)
                .retrieveMono(Item.class))
            .map(savedItem -> ResponseEntity.created(
                URI.create("/items/request-response")).body(savedItem));

    }

    @GetMapping(value = "/items/request-stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Item> findItemsUsingRSocketRequestStream() {
        return this.requester
            .flatMapMany(rSocketRequester -> rSocketRequester
                .route("newItems.request-stream")
                .retrieveFlux(Item.class)
                .delayElements(Duration.ofSeconds(1)));
    }

    @PostMapping(value = "/items/fire-and-forget")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
        return this.requester
            .flatMap(rSocketRequester -> rSocketRequester
                .route("newItems.fire-and-forget")
                .data(item)
                .send()
            .then(Mono.just(
                ResponseEntity.created(URI.create("/items/fire-and-forget")).build()
            )));
    }

    @GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Item> livedUpdates() {
        return this.requester
            .flatMapMany(rSocketRequester -> rSocketRequester
                .route("newItems.monitor")
                .retrieveFlux(Item.class));
    }

    @Async
    @GetMapping(value = "/asyncHttp")
    public CompletableFuture<String> asyncHttp() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
        WebClient client = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("https://m.naver.com");
        WebClient.RequestHeadersSpec headersSpec = bodySpec.body(
            BodyInserters.fromPublisher(Mono.just("data"), String.class)
        );

        Mono<String> response = headersSpec.retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(result -> System.out.println(String.format("Response Html : %s", result)))
            .doOnNext(System.out::print)
            .subscribeOn(Schedulers.immediate());

        System.out.println("Completed request");
        return response.toFuture();
        //futureHtml = response.toFuture().get(1000L, TimeUnit.MILLISECONDS);

        //return futureHtml;
    }
}
