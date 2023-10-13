package com.vialink.atlantis.authorizationserver.apikey;

import com.vialink.atlantis.authorizationserver.configuration.AuthorizationServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@Service
@RequiredArgsConstructor
public class ApiKeyDetailManager implements RegisteredClientRepository {

    private final ApiKeyRepository repository;

    private final AuthorizationServerProperties properties;

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    @Transactional
    public RegisteredClient findById(String id) {
        return repository.findById(UUID.fromString(id))
                .map(this::toRegisteredClient)
                .orElseThrow();
    }

    @Override
    @Transactional
    public RegisteredClient findByClientId(String clientId) {
        return repository.findByApplicationName(clientId)
                .map(this::toRegisteredClient)
                .orElseThrow();
    }

    private RegisteredClient toRegisteredClient(ApiKey apiKey) {
        RegisteredClient.Builder builder = RegisteredClient.withId(apiKey.getId().toString())
                .clientId(apiKey.getApplicationName())
                .clientSecret(apiKey.getSecret())
                .clientSecretExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .clientName(apiKey.getApplicationName())
                .clientAuthenticationMethods((authenticationMethods) -> authenticationMethods.add(CLIENT_SECRET_BASIC))
                .authorizationGrantTypes((grantTypes) -> grantTypes.addAll(getGrantTypes(apiKey)))
                .redirectUri(String.format(properties.getAuthorizationCode().getRedirectUri(), apiKey.getApplicationName()))
                .postLogoutRedirectUri(properties.getAuthorizationCode().getPostLogoutRedirectUri())
                .scopes((scopes) -> scopes.add("openid"))
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(apiKey.isAuthorizationCode())
                        .build())
                .tokenSettings(TokenSettings.builder().build());

        return builder.build();
    }

    private List<AuthorizationGrantType> getGrantTypes(ApiKey apiKey) {
        List<AuthorizationGrantType> grantTypes = new ArrayList<>();
        if (apiKey.isAuthorizationCode()) {
            grantTypes.addAll(List.of(
                    AuthorizationGrantType.AUTHORIZATION_CODE,
                    AuthorizationGrantType.REFRESH_TOKEN
            ));
        }
        if (apiKey.isClientCredentials()) {
            grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }
        return List.of(grantTypes.toArray(AuthorizationGrantType[]::new));
    }
}
