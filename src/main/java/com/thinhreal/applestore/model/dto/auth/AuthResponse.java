package com.thinhreal.applestore.model.dto.auth;

import com.thinhreal.applestore.model.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private final Long userId;
    private final String email;
    private final UserRole role;
    private final String message;
}
