package com.webflux.webflux;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WebfluxApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void given_flux_dish() {
        KitchenService kitchenService = new KitchenService();
        Flux<Dish> dishFlux =  kitchenService.getDishes()
            .doOnNext(dish -> System.out.println("Thank u for dish !"))
            .doOnError(error -> System.out.println("Sorry about " + error.getMessage()))
            .doOnComplete(() -> System.out.println("Thanks for all your hard work!"))
            .map(dish -> Dish.deliver(dish));

        dishFlux.subscribe();
    }

    @Test
    public void hooks_on_debug_test() {
        Hooks.onOperatorDebug();

        Mono<Integer> source;
        if (new Random().nextBoolean()) {
            source = Flux.range(1, 10).elementAt(5);
        }
        else {
            source = Flux.just(1,2,3,4,5).elementAt(10);
        }

        source.subscribeOn(Schedulers.parallel())
            .block();
    }

    @Test
    public void pubTest() {
        Flux.range(1, 100)
            .publishOn(Schedulers.parallel())
            .subscribe(v -> System.out.println(v));
    }

    @Test
    public void flux_create_test() {
        Flux<String> fluxStr = Flux.just("a", "b", "foobar");
        Flux<String> fluxIter = Flux.fromIterable(Arrays.asList("ab", "cd", "ef", "foobar"));
        Flux<Integer> numbers = Flux.range(0, 10);

        Mono<String> noData = Mono.empty();
        Mono<String> data = Mono.just("foobar");

    }

    @Test
    public void flux_generate_test() {
        Flux<Long> squares = Flux.generate(
            AtomicLong::new, (state, sink) -> {
                long i = state.getAndIncrement();
                sink.next(i * i);
                if (i == 10) sink.complete();
                return state;
            }
        );
        squares.subscribe(x -> System.out.println(x));
    }

    @Test
    public void flux_sink_push_test() {
        var pusher = Flux.push((FluxSink sink) -> {
            sink.next(1).next(2).next(3).complete();
        });

        pusher.subscribe(System.out::println);
    }

    @Test
    public void flux_scheduler_test() {
        List<Integer> squares = new ArrayList<>();
        Flux.range(1,64).flatMap(v -> Mono.just(v)
            .subscribeOn(Schedulers.newSingle("comp"))
            .map(w -> w * w))
            .doOnError(e -> e.printStackTrace())
            .doOnComplete(() -> System.out.println("Completed"))
            .subscribeOn(Schedulers.immediate())
            .subscribe(squares::add);

        squares.forEach(x -> System.out.println(x));
    }

    @Test
    public void flux_scheduler_parallel_test() {
        List<Integer> squares = new ArrayList<>();
        Flux.range(1,64).flatMap(v -> Mono.just(v)
            .subscribeOn(Schedulers.parallel())
            .map(w -> w * w))
            .doOnError(e -> e.printStackTrace())
            .doOnComplete(() -> System.out.println("Completed"))
            //.subscribeOn(Schedulers.parallel())
            .subscribe(squares::add);

        squares.forEach(x -> System.out.println(x));
    }

    @Test
    public void flux_context_test() {
        Flux<Integer> flux = Flux.just(1).subscriberContext(Context.of("pid", 123));
        Flux<String> stringFlux = flux.flatMap(i -> Mono.subscriberContext()
            .map(ctx -> i + " pid: " + ctx.getOrDefault("pid", 0)));

        StepVerifier.create(stringFlux)
            .expectNext("1 pid: 0")
            .verifyComplete();
    }

    @Test
    public void webclient_test() {
        HttpClient httpClient = HttpClient.create();
        WebClient client = WebClient.builder()
            .codecs(config -> config.defaultCodecs()
               .maxInMemorySize(2 * 1024 * 1024))
           .clientConnector(new ReactorClientHttpConnector(httpClient))
           .build();

        String test = client.get().uri("http://m.yes24.com").retrieve()
            .bodyToMono(String.class)
            .block();
        System.out.print(test);

        var monoRequest = client.get()
            .uri("http://m.yes24.com")
            .retrieve()
            .bodyToMono(String.class)
            .doOnSubscribe(s -> s.request(0))
            .delaySubscription(Duration.ofMillis(1000), Schedulers.newSingle("test-mono"));

        monoRequest.subscribe(c -> {
            System.out.print("#### content started ####\n" + c + "\n#### content closed ####");
        });



    }

    @Test
    public void test_publisher() {
        // arrange
        TestPublisher<Object> publisher = TestPublisher.create();
        Flux<Object> stringFlux = publisher.flux();
        List list = new ArrayList();

        // act
        stringFlux.subscribe(next -> list.add(next), ex -> ex.printStackTrace());
        publisher.emit("foo", "bar");

        // assert
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("bar", list.get(1));
    }

    @Test
    public void webclient_get_response_test() {
        // arrange
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

        // act
        Mono<String> response = headersSpec.retrieve()
            .bodyToMono(String.class);

        // assert
        var result = response.block(Duration.ofMillis(7000));
        assertNotNull(result);
        System.out.print(result);
    }

}
