package com.vialink.atlantis.authorizationserver.configuration;

import com.vialink.atlantis.api.ApiKeyInfo;
import com.vialink.atlantis.authorizationserver.service.ApiKeyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomAccessTokenSuccessHandler implements AuthenticationSuccessHandler {

    private final ApiKeyService apiKeyService;
    private final HttpMessageConverter<OAuth2AccessTokenResponse> converter = new OAuth2AccessTokenResponseHttpMessageConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AccessTokenAuthenticationToken token) {
            onSuccessfulOAuth2AccessTokenAuthentication(response, token);
        }
    }

    private void onSuccessfulOAuth2AccessTokenAuthentication(HttpServletResponse response, OAuth2AccessTokenAuthenticationToken token) throws IOException {
        Map<String, Object> additionalParameters = token.getAdditionalParameters();
        if (additionalParameters.size() == 0)
            additionalParameters = new HashMap<>();

        // Customize token response with api key info
        ApiKeyInfo apiKeyInfo = apiKeyService.findByApplicationName(token.getName())
                .map(apiKey -> ApiKeyInfo.builder()
                        .id(apiKey.getId())
                        .applicationName(apiKey.getApplicationName())
                        .email(apiKey.getEmail())
                        .build()).orElseThrow();
        additionalParameters.put("api_key_info", apiKeyInfo);

        var accessToken = token.getAccessToken();
        var refreshToken = token.getRefreshToken();
        var builder = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                .tokenType(accessToken.getTokenType())
                .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        builder.additionalParameters(additionalParameters);

        var r = builder.build();
        var httpResponse = new ServletServerHttpResponse(response);
        this.converter.write(r, null, httpResponse);
    }
}
