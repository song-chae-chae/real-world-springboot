package com.chaechae.realworldspringboot.security.jwt.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class TokenException extends RealWorldException {
    private final TokenExceptionType tokenExceptionType;

    public TokenException(TokenExceptionType tokenExceptionType) {
        this.tokenExceptionType = tokenExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return tokenExceptionType;
    }
}
