package com.example.reactive.service;

import com.example.reactive.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class ReactiveServiceApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void eventById() {
        webTestClient.get()
                .uri("/events/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class);
    }

    @Test
    void events() {
        webTestClient.get()
                .uri("/events")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Event.class);
    }

    @Test
    void strings() {
        webTestClient.get()
                .uri("/strings")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(String.class);
    }
}