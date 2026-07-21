package com.thinhreal.applestore.security;

import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    public static final String TOKEN_TYPE_CLAIM = "type";
    public static final String ROLE_CLAIM = "role";
    public static final String EMAIL_CLAIM = "email";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    public String generateAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenExpirationSeconds());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(ROLE_CLAIM, user.getRole().name())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public RefreshTokenPayload generateRefreshToken(UserEntity user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds());
        String tokenId = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .id(tokenId)
                .subject(user.getId().toString())
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();

        return new RefreshTokenPayload(tokenId, token, expiry, hashToken(token));
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public Authentication buildAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        String role = claims.get(ROLE_CLAIM, String.class);
        String email = claims.get(EMAIL_CLAIM, String.class);
        String userId = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + UserRole.valueOf(role).name())
        );

        AuthenticatedUser principal = new AuthenticatedUser(Long.parseLong(userId), email, UserRole.valueOf(role));
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public String extractRefreshTokenId(String refreshToken) {
        return parseClaims(refreshToken).getId();
    }

    public Instant extractExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.toInstant();
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    public long getAccessTokenMaxAgeSeconds() {
        return jwtProperties.getAccessTokenExpirationSeconds();
    }

    public long getRefreshTokenMaxAgeSeconds() {
        return jwtProperties.getRefreshTokenExpirationSeconds();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record RefreshTokenPayload(
            String tokenId,
            String token,
            Instant expiryDate,
            String tokenHash
    ) {
    }

    public record AuthenticatedUser(Long id, String email, UserRole role) {
    }
}
