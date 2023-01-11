package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentExceptionType implements RealWorldExceptionType {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "댓글에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    CommentExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
