package com.thinhreal.applestore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.thinhreal.applestore.model.dto.exceptionResponse.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //@Aspect duoc custom voi default Advice: After return
public class GlobalExceptionHandler {
    // Business Exception
    @ExceptionHandler(BusinessException.class) // Pointcut
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException err) {
        // Create a New DTO response
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                err.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // System exception
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystemException(SystemException err) {

        ErrorResponse sysBody = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                err.getMessage());
        return new ResponseEntity<>(sysBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
