package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArticleExceptionType implements RealWorldExceptionType {
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ArticleExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
