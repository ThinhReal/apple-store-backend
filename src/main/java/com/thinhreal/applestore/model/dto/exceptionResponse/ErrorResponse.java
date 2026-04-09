package com.thinhreal.applestore.model.dto.exceptionResponse;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;

    public ErrorResponse (int status, String message){
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
