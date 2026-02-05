package com.azmew.connector.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class MetaService {

    private final WebClient webClient;
    private final String appId;
    private final String appSecret;
    private final String redirectUri;

    public MetaService() {
        Dotenv dotenv = Dotenv.load();
        this.webClient = WebClient.builder().baseUrl("https://graph.facebook.com/v18.0").build();
        this.appId = dotenv.get("FACEBOOK_APP_ID");
        this.appSecret = dotenv.get("FACEBOOK_APP_SECRET");
        this.redirectUri = dotenv.get("FACEBOOK_REDIRECT_URI");
    }

    public Mono<Map> exchangeCodeForToken(String code) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/access_token")
                        .queryParam("client_id", appId)
                        .queryParam("client_secret", appSecret)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnSuccess(res -> log.info("Successfully exchanged Facebook code for token"))
                .doOnError(err -> log.error("Failed to exchange Facebook code: {}", err.getMessage()));
    }

    public Mono<Map> getPages(String userAccessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/accounts")
                        .queryParam("access_token", userAccessToken)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnSuccess(res -> log.info("Successfully fetched Facebook pages"))
                .doOnError(err -> log.error("Failed to fetch Facebook pages: {}", err.getMessage()));
    }
}
