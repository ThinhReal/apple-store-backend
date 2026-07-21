package com.thinhreal.applestore.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "change-this-dev-secret-key-to-at-least-32-characters";
    private long accessTokenExpirationSeconds = 900;
    private long refreshTokenExpirationSeconds = 604_800;
}
