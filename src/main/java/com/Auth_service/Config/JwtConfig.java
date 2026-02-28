package com.Auth_service.Config;

import com.Auth_service.util.RsaKeyLoader;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtConfig {

    @Bean
    public RSAPrivateKey jwtPrivateKey() {
        // adjust path if different
        return RsaKeyLoader.loadPrivateKeyFromPem("/private.pem");
    }
    @Bean
    public RSAPublicKey jwtPublicKey() {
        // adjust path if different
        return RsaKeyLoader.loadPublicKeyFromPem("/public.pem");
    }


    @Bean
    public JwtEncoder jwtEncoder(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        RSAKey rsa = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
        var jwkSource = (com.nimbusds.jose.jwk.source.JWKSource<com.nimbusds.jose.proc.SecurityContext>)
            (selector, ctx) -> selector.select(new JWKSet(rsa));
        return new NimbusJwtEncoder(jwkSource);
    }

}
