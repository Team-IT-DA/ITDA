package com.itda.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NaverLoginService implements OauthProvider {

    private final String GRANT_TYPE = "authorization_code";

    @Value("{oauth.naver.client_id}")
    private String clientId;

    @Value("{oauth.naver.secret}")
    private String clientSecret;

    private final WebClient webClient;

    @Override
    public void login() {

    }

    public NaverAccessToken getAccessToken(String code) {

        Mono<NaverAccessToken> naverAccessTokenMono = webClient.mutate()
                .build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host("nid.naver.com")
                                .path("/oauth2.0/token")
                                .queryParam("grant_type", GRANT_TYPE)
                                .queryParam("client_id", clientId)
                                .queryParam("client_secret", clientSecret)
                                .queryParam("code", code)
                                .build())
                .retrieve()
                .bodyToMono(NaverAccessToken.class);

        return naverAccessTokenMono.block();
    }
}
