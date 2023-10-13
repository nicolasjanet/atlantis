package com.vialink.atlantis.resourceserver;

import com.vialink.atlantis.api.Foo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping(FooController.PREFIX)
@Slf4j
public class FooController {

    public static final String PREFIX = "/foos";
    public static final UUID UUID = randomUUID();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Foo>> get(Authentication authentication) {
        log.info("{} requests resource", authentication.getName());
        return Mono.just(Foo.builder()
                        .id(UUID)
                        .build())
                .map(ResponseEntity::ok);
    }

}
