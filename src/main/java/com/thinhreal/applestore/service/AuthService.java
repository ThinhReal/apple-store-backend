package com.thinhreal.applestore.service;

import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.dto.auth.AuthResponse;
import com.thinhreal.applestore.model.dto.auth.LoginRequest;
import com.thinhreal.applestore.model.dto.auth.RegisterRequest;
import com.thinhreal.applestore.model.entity.RefreshTokenEntity;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.UserRole;
import com.thinhreal.applestore.repository.RefreshTokenRepository;
import com.thinhreal.applestore.repository.UserRepository;
import com.thinhreal.applestore.security.CookieService;
import com.thinhreal.applestore.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateAccessToken(user);
        JwtService.RefreshTokenPayload refreshPayload = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshPayload);

        cookieService.writeAuthCookies(
                response,
                accessToken,
                refreshPayload.token(),
                jwtService.getAccessTokenMaxAgeSeconds(),
                jwtService.getRefreshTokenMaxAgeSeconds()
        );

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("An account with this email already exists");
        }

        UserEntity user = new UserEntity(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getAddress()
        );
        user.setRole(UserRole.CUSTOMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateAccessToken(user);
        JwtService.RefreshTokenPayload refreshPayload = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshPayload);

        cookieService.writeAuthCookies(
                response,
                accessToken,
                refreshPayload.token(),
                jwtService.getAccessTokenMaxAgeSeconds(),
                jwtService.getRefreshTokenMaxAgeSeconds()
        );

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Account created successfully")
                .build();
    }

    @Transactional
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.readCookie(request, JwtService.REFRESH_TOKEN_COOKIE)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new BadCredentialsException("Refresh token is invalid or expired");
        }

        String tokenId = jwtService.extractRefreshTokenId(refreshToken);
        RefreshTokenEntity storedToken = refreshTokenRepository.findByTokenIdAndRevokedFalse(tokenId)
                .orElseThrow(() -> new BadCredentialsException("Refresh token has been revoked"));

        if (!storedToken.getTokenHash().equals(jwtService.hashToken(refreshToken))) {
            throw new BadCredentialsException("Refresh token is invalid");
        }

        UserEntity user = storedToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);

        cookieService.writeAuthCookies(
                response,
                accessToken,
                refreshToken,
                jwtService.getAccessTokenMaxAgeSeconds(),
                jwtService.getRefreshTokenMaxAgeSeconds()
        );

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Access token refreshed")
                .build();
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.readCookie(request, JwtService.REFRESH_TOKEN_COOKIE).ifPresent(refreshToken -> {
            if (jwtService.isRefreshTokenValid(refreshToken)) {
                String tokenId = jwtService.extractRefreshTokenId(refreshToken);
                refreshTokenRepository.findByTokenIdAndRevokedFalse(tokenId)
                        .ifPresent(token -> token.setRevoked(true));
            }
        });

        cookieService.clearAuthCookies(response);

        return AuthResponse.builder()
                .message("Logout successful")
                .build();
    }

    public AuthResponse currentUser(HttpServletRequest request) {
        return cookieService.readCookie(request, JwtService.ACCESS_TOKEN_COOKIE)
                .filter(jwtService::isAccessTokenValid)
                .map(token -> {
                    JwtService.AuthenticatedUser user = (JwtService.AuthenticatedUser)
                            jwtService.buildAuthentication(token).getPrincipal();
                    return AuthResponse.builder()
                            .userId(user.id())
                            .email(user.email())
                            .role(user.role())
                            .message("Authenticated")
                            .build();
                })
                .orElseThrow(() -> new BadCredentialsException("Not authenticated"));
    }

    private void persistRefreshToken(UserEntity user, JwtService.RefreshTokenPayload refreshPayload) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenId(refreshPayload.tokenId());
        refreshTokenEntity.setTokenHash(refreshPayload.tokenHash());
        refreshTokenEntity.setExpiryDate(refreshPayload.expiryDate());
        refreshTokenEntity.setRevoked(false);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
