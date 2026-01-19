package io.github.pansai.demo_20260107.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.pansai.demo_20260107.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 在 Service 里主动抛出的参数/业务错误 -> 400
   @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest req
   ){
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(new ErrorResponse(
                       "BAD_REQUEST",
                       ex.getMessage(),
                       req.getRequestURI(),
                       Instant.now()
               ));
   }

    // 数据库约束冲突（unique 等） -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "CONFLICT",
                        "Database constraint violated",
                        req.getRequestURI(),
                        Instant.now()
                ));
    }

    // 兜底 -> 500（生产环境建议隐藏细节）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOthers(
            Exception ex,
            HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "Internal server error",
                        req.getRequestURI(),
                        Instant.now()
                ));
    }

}
