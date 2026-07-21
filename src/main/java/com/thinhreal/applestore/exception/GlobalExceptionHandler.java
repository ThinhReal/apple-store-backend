package com.thinhreal.applestore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.thinhreal.applestore.model.dto.exceptionResponse.ErrorResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //@Aspect duoc custom voi default Advice: After return
public class GlobalExceptionHandler {

    private static final String OPTIMISTIC_LOCK_MESSAGE =
            "The product stock has been updated by another user. Please review your cart.";

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request data");
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You do not have permission to perform this action"
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), OPTIMISTIC_LOCK_MESSAGE);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
