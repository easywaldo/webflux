package com.webflux.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

}
