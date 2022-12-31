package com.chaechae.realworldspringboot.base.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RestTemplateExceptionType implements RealWorldExceptionType {
    INVALID_REST_API_CALL(HttpStatus.BAD_REQUEST, "잘못된 REST API 요청입니다.");

    private final HttpStatus status;
    private final String message;

    RestTemplateExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
