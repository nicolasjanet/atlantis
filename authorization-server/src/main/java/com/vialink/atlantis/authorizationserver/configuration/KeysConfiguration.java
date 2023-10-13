package com.vialink.atlantis.authorizationserver.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

@Configuration
@RequiredArgsConstructor
public class KeysConfiguration {

    private final AuthorizationServerProperties properties;

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        var keyPair = keyPair();
        var rsa = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(properties.getJwtSigner().getKeyId())
                .build();
        var jwk = new JWKSet(rsa);
        return new ImmutableJWKSet<>(jwk);
    }

    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                properties.getJwtSigner().getKeyStore(),
                properties.getJwtSigner().getKeyStorePassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair(properties.getJwtSigner().getKeyAlias());
    }
}
