package com.chaechae.realworldspringboot.user.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class UserException extends RealWorldException {
    private final UserExceptionType userExceptionType;

    public UserException(UserExceptionType userExceptionType) {
        this.userExceptionType = userExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return userExceptionType;
    }
}
