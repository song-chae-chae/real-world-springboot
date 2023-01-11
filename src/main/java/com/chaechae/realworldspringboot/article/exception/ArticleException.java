package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class ArticleException extends RealWorldException {
    private final ArticleExceptionType articleExceptionType;

    public ArticleException(ArticleExceptionType articleExceptionType) {
        this.articleExceptionType = articleExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return this.articleExceptionType;
    }
}
