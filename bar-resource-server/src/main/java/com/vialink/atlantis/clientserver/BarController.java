package com.vialink.atlantis.clientserver;

import com.vialink.atlantis.api.Bar;
import com.vialink.atlantis.api.Foo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(BarController.PREFIX)
@RequiredArgsConstructor
@Slf4j
public class BarController {

    public static final String PREFIX = "/bars";

    private final WebClient webClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bar>> get(Authentication authentication) {
        log.info("{} requests resource", authentication.getName());
        return this.webClient
                .get()
                .uri("http://localhost:8081/foos")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId("client"))
                .retrieve()
                .bodyToMono(Foo.class)
                .map(foo -> {
                    return Bar.builder()
                            .baz(RandomStringUtils.randomAlphanumeric(10))
                            .foo(foo)
                            .build();
                })
                .map(ResponseEntity::ok);
    }
}
