package com.vialink.atlantis.authorizationserver.configuration;

import com.vialink.atlantis.api.ApiKeyInfo;
import com.vialink.atlantis.api.UserInfo;
import com.vialink.atlantis.authorizationserver.service.ApiKeyService;
import com.vialink.atlantis.authorizationserver.service.SecurityContextService;
import com.vialink.atlantis.authorizationserver.service.UserService;
import com.vialink.atlantis.authorizationserver.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AuthorizationServerProperties.class)
@RequiredArgsConstructor
public class AuthorizationServerConfiguration {

    private final UserService userService;
    private final ApiKeyService apiKeyService;
    private final SecurityContextService securityContextService;

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(endPoint -> endPoint.accessTokenResponseHandler(new CustomAccessTokenSuccessHandler(
                        apiKeyService
                )))
                .oidc(Customizer.withDefaults());
        http.oauth2ResourceServer((resourceServer) -> {
            resourceServer.jwt(Customizer.withDefaults());
        });
        http.exceptionHandling((exceptions) -> {
            exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    createRequestMatcher());
        });
        http.cors(Customizer.withDefaults());
        return http.build();
    }

    private static RequestMatcher createRequestMatcher() {
        MediaTypeRequestMatcher requestMatcher = new MediaTypeRequestMatcher(new MediaType[]{MediaType.TEXT_HTML});
        requestMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
        return requestMatcher;
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/mfa").hasRole("MFA")
                        .anyRequest().authenticated())
                .formLogin(formLogin -> {
                    formLogin
                            .loginPage("/login")
                            .permitAll()
                            .successHandler(new CustomLoginSuccessHandler(securityContextService));
                })
                .cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(false)
                .ignoring()
                .requestMatchers("/webjars/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico");
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();

            // Customize access token with api key info and authorities
            if (context.getTokenType().equals(ACCESS_TOKEN)) {
                context.getClaims().claims(c -> {
                    apiKeyService.findByApplicationName(principal.getName())
                            .ifPresent(apiKey -> {
                                c.put("api_key_info", ApiKeyInfo.builder()
                                        .id(apiKey.getId())
                                        .applicationName(apiKey.getApplicationName())
                                        .email(apiKey.getEmail())
                                        .build());
                                c.put("authorities", apiKey.getRoles());
                            });
                });

            // Customize id token with authorities and user info
            } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                context.getClaims().claims(c -> {
                    c.put("authorities", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()));
                    userService.findByEmail(principal.getName())
                            .ifPresent(user -> {
                                c.put("user_info", UserInfo.builder()
                                        .id(user.getId())
                                        .spaceId(user.getSpaceId())
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
                                        .email(user.getEmail())
                                        .phone(user.getPhone())
                                        .roles(user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet()))
                                        .build());
                            });
                });

            }
        };
    }
}
