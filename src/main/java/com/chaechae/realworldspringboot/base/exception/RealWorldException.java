package com.chaechae.realworldspringboot.base.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
public abstract class RealWorldException extends RuntimeException {
    public Map<String, String> validation;

    public final RealWorldExceptionType realWorldExceptionType;

    public RealWorldException(RealWorldExceptionType realWorldExceptionType) {
        this.realWorldExceptionType = realWorldExceptionType;
    }
}
