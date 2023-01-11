package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FavoriteExceptionType implements RealWorldExceptionType {
    FAVORITE_ALREADY_DONE(HttpStatus.BAD_REQUEST, "이미 좋아요 되어있습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 되어있지 않습니다."),
    FAVORITE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "좋아요에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    FavoriteExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
