package com.chaechae.realworldspringboot.profile.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;

public class FollowException extends RealWorldException {
    private final FollowExceptionType followExceptionType;

    public FollowException(FollowExceptionType followExceptionType) {
        this.followExceptionType = followExceptionType;
    }

    @Override
    public RealWorldExceptionType getExceptionType() {
        return followExceptionType;
    }
}
