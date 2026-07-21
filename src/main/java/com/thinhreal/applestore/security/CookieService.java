package com.thinhreal.applestore.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieProperties cookieProperties;

    public void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken,
                                 long accessMaxAgeSeconds, long refreshMaxAgeSeconds) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                JwtService.ACCESS_TOKEN_COOKIE,
                accessToken,
                accessMaxAgeSeconds
        ).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                JwtService.REFRESH_TOKEN_COOKIE,
                refreshToken,
                refreshMaxAgeSeconds
        ).toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                JwtService.ACCESS_TOKEN_COOKIE,
                "",
                0
        ).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                JwtService.REFRESH_TOKEN_COOKIE,
                "",
                0
        ).toString());
    }

    public Optional<String> readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite(cookieProperties.getSameSite())
                .build();
    }
}
