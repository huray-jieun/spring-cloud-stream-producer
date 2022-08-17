package com.example.springcloudstreamrabbit.controller;


import com.example.springcloudstreamrabbit.data.MyMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    EmitterProcessor<MyMessage> directProcessor = EmitterProcessor.create();

    EmitterProcessor<MyMessage> broadcastProcessor = EmitterProcessor.create();

    @GetMapping(value = "/direct/{message}")
    public Mono<Void> directMessage(@PathVariable String message) {

        return Mono.just(message)
                .doOnNext(s -> directProcessor.onNext(MyMessage.builder().message(message).build()))
                .then();
    }

    @GetMapping(value = "/broadcast/{message}")
    public Mono<Void> broadcastMessage(@PathVariable String message) {

        return Mono.just(message)
                .doOnNext(s -> broadcastProcessor.onNext(MyMessage.builder().message(message).build()))
                .then();
    }

    @Bean
    public Supplier<Flux<MyMessage>> direct() {
        return () -> this.directProcessor;
    }

    @Bean
    public Supplier<Flux<MyMessage>> broadcast() {
        return () -> this.broadcastProcessor;
    }
}