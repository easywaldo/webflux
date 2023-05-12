package com.webflux.webflux;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class CompletionTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void test_then_accept() throws InterruptedException {
        log.info("Start main");
        CompletionStage<Integer> stage = CompletableFuture.supplyAsync(() -> {
            return 100;
        }).thenApply(s -> {
            return 200;
        }).thenApply(s -> {
            return 1000;
        });

        stage.thenAccept(i -> {
            log.info("First");
            System.out.println(i);
        }).thenAccept(i -> {
            log.info("Second");
            System.out.println(i);
        });
        Thread.sleep(2000);
    }

    @Test
    void test_then_accept_async() throws InterruptedException {
        log.info("Start main");
        CompletionStage<Integer> stage = CompletableFuture.supplyAsync(() -> {
            return 100;
        }).thenApplyAsync(s -> {
            return 200;
        }).thenApplyAsync(s -> {
            return 1000;
        });

        stage.thenAcceptAsync(i -> {
            log.info("First");
            System.out.println(i);
        }).thenAcceptAsync(i -> {
            log.info("Second");
            System.out.println(i);
        });
        Thread.sleep(2000);
    }

    @Test
    void complete_test() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        assert !future.isDone();

        var triggered = future.complete(1);
        assert future.isDone();
        assert triggered;
        assert future.get() == 1;

        triggered = future.complete(2);
        assert future.isDone();
        assert !triggered;
        assert future.get() == 1;


    }
}
