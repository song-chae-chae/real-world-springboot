package com.chaechae.realworldspringboot.base.exception;

import lombok.Getter;

@Getter
public enum RestTemplateExceptionType implements RealWorldExceptionType {
    INVALID_REST_API_CALL(400, "잘못된 REST API 요청입니다.");

    private final int statusCode;
    private final String message;

    RestTemplateExceptionType(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
