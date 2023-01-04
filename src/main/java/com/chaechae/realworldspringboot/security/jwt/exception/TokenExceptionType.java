package com.chaechae.realworldspringboot.security.jwt.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenExceptionType implements RealWorldExceptionType {
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 토큰입니다.");

    private final HttpStatus status;
    private final String message;

    TokenExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
