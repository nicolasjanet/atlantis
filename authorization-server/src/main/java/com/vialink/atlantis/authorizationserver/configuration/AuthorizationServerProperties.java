package com.vialink.atlantis.authorizationserver.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = AuthorizationServerProperties.PREFIX)
@Validated
public class AuthorizationServerProperties {

    public static final String PREFIX = "authorization-server";

    @NestedConfigurationProperty
    private JwtSignerProperties jwtSigner;

    @NestedConfigurationProperty
    private AuthorizationCode authorizationCode;

    private String scope;

    @Getter
    @Setter
    public static class AuthorizationCode {
        private String redirectUri;
        private String postLogoutRedirectUri;
    }

    @Getter
    @Setter
    public static class JwtSignerProperties {

        /**
         * Path to JKS holding an RSA keypair.
         */
        private Resource keyStore;

        /**
         * Password of the JKS and private key.
         */
        private String keyStorePassword;

        /**
         * Alias of the keypair to use for JWT signature.
         */
        private String keyAlias;

        /**
         * Kid of the key
         */
        private String keyId = "jwt-signer-1";
    }
}
