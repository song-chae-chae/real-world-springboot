package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class CommentException extends RealWorldException {
    private final CommentExceptionType commentExceptionType;
    public CommentException(CommentExceptionType commentExceptionType) {
        this.commentExceptionType = commentExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return commentExceptionType;
    }
}
