package com.vialink.atlantis.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthorizeController {

    @GetMapping(value = "/authorize", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<ResponseEntity<Void>> authorize() {
        ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:8083")
                .build();
        return Mono.just(responseEntity);
    }

}
