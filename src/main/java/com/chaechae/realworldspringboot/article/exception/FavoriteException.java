package com.chaechae.realworldspringboot.article.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class FavoriteException extends RealWorldException {
    private final FavoriteExceptionType favoriteExceptionType;

    public FavoriteException(FavoriteExceptionType favoriteExceptionType) {
        this.favoriteExceptionType = favoriteExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return favoriteExceptionType;
    }
}
