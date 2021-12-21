package com.example.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple Websocket server forwarding incoming messages to all connected clients
 */
@SpringBootApplication
@EnableScheduling
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}

@Configuration
@EnableWebFlux
class WebSocketConfiguration implements WebFluxConfigurer {

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/messages", new MyWebSocketHandler());
        return new SimpleUrlHandlerMapping(map);
    }
}

class MyWebSocketHandler implements WebSocketHandler {

    // https://projectreactor.io/docs/core/release/reference/#processors
    Sinks.Many<String> replaySink = Sinks.many().replay().limit(10); // limit to the last 10 messages

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        // handles inbound and outbound messages
        return Mono.zip(
                session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .log()
                    .doOnNext(message -> replaySink.tryEmitNext(message))
                    .then(),
                session.send(
                        replaySink
                                .asFlux()
                                .map(session::textMessage)
                                .log())
        ).then();
    }
}