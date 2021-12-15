package com.example.reactive.service;

import com.example.reactive.model.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
public class ReactiveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveServiceApplication.class, args);
    }

    @GetMapping("/events/{id}")
    Mono<Event> eventById(@PathVariable long id) {
        return Mono.just(new Event(UUID.randomUUID(), Instant.now(), "any message"));
    }

    // APPLICATION_NDJSON_VALUE
    // or
    // TEXT_EVENT_STREAM_VALUE (https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events)
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        return Flux.interval(Duration.ofSeconds(new Random().nextInt(5)))
                .take(50)
                .map(i -> new Event(UUID.randomUUID(), Instant.now(), "any message "+i));
    }

    @GetMapping(value = "/strings", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> strings() {
        return Flux.fromStream(Stream.of("foo", "bar", "baz"));
    }
}
