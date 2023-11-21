package com.hellocorp.example.common.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Result VO
 */
@Data
public class Result<T> {

    private boolean success;

    private Integer errCode;

    private String message;

    private T data;

    public static <T> Result<T> fail(Integer errCode, String message) {
        Result<T> result = new Result<>();
        result.setErrCode(errCode);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> success(T t) {
        Result<T> result = new Result<>();
        result.setData(t);
        result.setMessage(HttpStatus.OK.getReasonPhrase());
        result.setSuccess(true);
        return result;
    }

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.setMessage(HttpStatus.OK.getReasonPhrase());
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> timeout() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());
        result.setErrCode(HttpStatus.REQUEST_TIMEOUT.value());
        return result;
    }

    public static <T> Result<T> notFound() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        result.setErrCode(HttpStatus.NOT_FOUND.value());
        return result;
    }
}
