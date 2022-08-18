package com.example.springcloudstreamrabbit.controller;


import com.example.springcloudstreamrabbit.data.MyMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

@RestController
@Slf4j
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    @Autowired
    private Sinks.Many<Message<MyMessage>> many;

    @GetMapping(value = "/direct/{message}")
    public Mono<Void> directMessage(@PathVariable String message) throws IOException {
        return Mono.just(message)
                .doOnNext(m -> {
                    MyMessage myMessage = MyMessage.builder().message(m).build();
                    many.emitNext(MessageBuilder.withPayload(myMessage).build(), Sinks.EmitFailureHandler.FAIL_FAST);
                })
                .doOnNext(m -> log.info("send {}", m))
                .then();
    }

}