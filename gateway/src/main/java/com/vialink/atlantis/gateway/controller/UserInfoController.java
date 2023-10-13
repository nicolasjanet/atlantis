package com.vialink.atlantis.gateway.controller;

import com.vialink.atlantis.gateway.dto.UserDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public Mono<UserDTO> userInfo(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> userInfo = principal.getIdToken().getClaimAsMap("user_info");
        return Mono.just(
                new UserDTO(
                        principal.getIdToken().getClaimAsStringList("authorities"),
                        (String) userInfo.get("firstName"),
                        (String) userInfo.get("lastName"),
                        principal.getIdToken().getSubject()
                ));
    }

}
