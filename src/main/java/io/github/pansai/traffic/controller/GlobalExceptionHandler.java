package io.github.pansai.traffic.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.pansai.traffic.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // service failed 400
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURI(), Instant.now());
    }

    // database constraint conflict (example: unique) 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return new ErrorResponse("CONFLICT", "Database constraint violated", request.getRequestURI(), Instant.now());
    }

    // state error 403
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(IllegalStateException ex, HttpServletRequest request) {
        return new ErrorResponse("FORBIDDEN", ex.getMessage(), request.getRequestURI(), Instant.now());
    }

    // else 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleForbidden(Exception ex, HttpServletRequest request) {
        return new ErrorResponse("INTERNAL_ERROR", "Internal server error", request.getRequestURI(), Instant.now());
    }
}
