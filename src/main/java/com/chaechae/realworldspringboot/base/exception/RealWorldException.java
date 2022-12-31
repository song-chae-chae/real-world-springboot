package com.chaechae.realworldspringboot.base.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class RealWorldException extends RuntimeException {
    public Map<String, Object> validation = new HashMap<>();

    public abstract RealWorldExceptionType getExceptionType();
}
