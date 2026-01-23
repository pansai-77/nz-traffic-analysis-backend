package io.github.pansai.traffic.handler;

import io.github.pansai.traffic.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCode code = ex.getErrorCode();

        ApiResponse<Void> body = ApiResponse.fail(code);
        body.setTraceId(MDC.get("traceId"));

        return ResponseEntity.status(code.httpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleSystem(Exception ex) {
        ApiResponse<Void> body = ApiResponse.fail(ErrorCode.SYSTEM_ERROR);
        body.setTraceId(MDC.get("traceId"));

        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.httpStatus()).body(body);
    }
}
