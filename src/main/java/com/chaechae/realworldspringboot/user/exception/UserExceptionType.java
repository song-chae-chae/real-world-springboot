package com.chaechae.realworldspringboot.user.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionType implements RealWorldExceptionType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    UserExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
