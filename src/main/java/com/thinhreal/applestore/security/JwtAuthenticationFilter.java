package com.thinhreal.applestore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            cookieService.readCookie(request, JwtService.ACCESS_TOKEN_COOKIE)
                    .filter(jwtService::isAccessTokenValid)
                    .ifPresent(token -> SecurityContextHolder.getContext()
                            .setAuthentication(jwtService.buildAuthentication(token)));
        }

        filterChain.doFilter(request, response);
    }
}
