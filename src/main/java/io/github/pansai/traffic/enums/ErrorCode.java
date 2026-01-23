package io.github.pansai.traffic.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUCCESS(HttpStatus.OK, "success"),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    //request error
    REQ_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"Unauthorized"),
    REQ_FORBIDDEN(HttpStatus.FORBIDDEN,"Forbidden"),

    //user register or activate or resendEmail or login business
    USER_REGISTER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"Email already exists"),
    USER_ACTIVATION_TOKEN_NOT_EXISTS(HttpStatus.BAD_REQUEST,"Activation token does not exist"),
    USER_ACTIVATION_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST,"Activation token expired"),
    USER_RESEND_USER_NOT_EXISTS(HttpStatus.BAD_REQUEST,"User not found"),
    USER_RESEND_ALREADY_ACTIVATED(HttpStatus.BAD_REQUEST,"User already activated"),
    USER_LOGIN_INFO_INVALID(HttpStatus.BAD_REQUEST,"Invalid email or password"),
    USER_LOGIN_NOT_ACTIVATE(HttpStatus.FORBIDDEN,"User not activated"),

    // user auth business
    USER_AUTH_USER_NOT_EXISTS_ERR(HttpStatus.UNAUTHORIZED,"User not found"),
    USER_AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"Login token expired"),
    USER_AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED,"Invalid login token"),
    USER_AUTH_TOKEN_VALID_ERROR(HttpStatus.UNAUTHORIZED,"Login token validation error")
    ;

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String defaultMessage() {
        return defaultMessage != null ? defaultMessage : this.name();
    }
}
