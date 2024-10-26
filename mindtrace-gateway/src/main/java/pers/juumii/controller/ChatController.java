package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.util.Objects;

@RestController
public class ChatController {

    private final WebClient.Builder builder;

    @Autowired
    public ChatController(WebClient.Builder webClientBuilder) {
        this.builder = webClientBuilder;
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> forwardStream(@RequestBody Map<String, Object> clientData, ServerHttpRequest request) {
        HttpCookie apiKeyCookie = request.getCookies().getFirst("OPENAI_API_KEY");
        HttpCookie proxyCookie = request.getCookies().getFirst("OPENAI_PROXY_BASE");
        String apiKey = Objects.isNull(apiKeyCookie) ? System.getenv("OPENAI_API_KEY") : apiKeyCookie.getValue();
        String proxy = Objects.isNull(proxyCookie) ? System.getenv("OPENAI_PROXY_BASE") : proxyCookie.getValue();
        WebClient webClient = builder
                .baseUrl(proxy+"/v1/chat/completions")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        return webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientData)
                .retrieve()
                .bodyToFlux(String.class)
                .map(data -> ServerSentEvent.builder(data).build())
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).build()));
    }
}
