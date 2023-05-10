package com.webflux.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@SpringBootTest
public class CompletionTest {
    @Test
    void test_then_accept() throws InterruptedException {
        System.out.println("start main");
        CompletionStage<Integer> stage = CompletableFuture.supplyAsync(() -> {
            return 100;
        }).thenApply(s -> {
            return 200;
        }).thenApply(s -> {
            return 1000;
        });

        stage.thenAccept(i -> {
            System.out.println(i);
        }).thenAccept(i -> {
            System.out.println(i);
        });
        Thread.sleep(2000);
    }
}
