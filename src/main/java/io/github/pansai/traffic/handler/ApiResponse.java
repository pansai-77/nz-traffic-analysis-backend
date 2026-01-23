package io.github.pansai.traffic.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pansai.traffic.enums.ErrorCode;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ErrorCode code;
    private String message;
    private T data;
    private String traceId;

    private ApiResponse() {}

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();

        ErrorCode code = ErrorCode.SUCCESS;
        r.code = code;
        r.message = code.defaultMessage();
        r.data = data;

        return r;
    }

    public static ApiResponse<Void> success(ErrorCode code) {
        ApiResponse<Void> r = new ApiResponse<>();
        r.setCode(code);
        r.setMessage(code.defaultMessage());
        return r;
    }

    public static ApiResponse<Void> fail(ErrorCode code) {
        ApiResponse<Void> r = new ApiResponse<>();
        r.setCode(code);
        r.setMessage(code.defaultMessage());
        return r;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
